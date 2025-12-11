package com.kudiapp.kudiapp.services.serviceImpl.productSeervice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kudiapp.kudiapp.config.PaystackHttpClient;
import com.kudiapp.kudiapp.config.paystack.PaystackConfig;
import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.InitPaymentRequest;
import com.kudiapp.kudiapp.dto.productService.InitPaymentResponse;
import com.kudiapp.kudiapp.dto.request.payment.VerifyResponseDto;
import com.kudiapp.kudiapp.enums.productService.CartStatus;
import com.kudiapp.kudiapp.enums.productService.OrderAction;
import com.kudiapp.kudiapp.enums.productService.OrderStatus;
import com.kudiapp.kudiapp.exceptions.PaymentException;
import com.kudiapp.kudiapp.exceptions.ResourceNotFoundException;
import com.kudiapp.kudiapp.models.productService.Cart;
import com.kudiapp.kudiapp.repository.CartRepository;
import com.kudiapp.kudiapp.repository.OrderRepository;
import com.kudiapp.kudiapp.services.productService.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaystackHttpClient paystackHttpClient;
    private final PaystackConfig paystackConfig;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public InitPaymentResponse initializePayment(InitPaymentRequest request) {
        log.info("Initializing payment for cart reference: {}", request.getCartReference());

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            log.error("Initialization failed: email is missing");
            throw new PaymentException("Email is required");
        }

        // Find cart by reference
        Cart cart = cartRepository.findByCartReference(request.getCartReference())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found with reference: " + request.getCartReference()));

        // Validate cart is checked out
        if (!CartStatus.CHECKED_OUT.equals(cart.getStatus())) {
            throw new PaymentException("Cart must be checked out before payment");
        }

        // Validate cart has payment reference
        if (cart.getPaymentReference() == null) {
            throw new PaymentException("Cart does not have a payment reference");
        }

        // Calculate amount in kobo (Paystack uses lowest currency unit)
        long amountInKobo = cart.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        // Prepare Paystack request body
        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("amount", amountInKobo);
        body.put("currency", cart.getCurrency().name());
        body.put("reference", cart.getPaymentReference());
        body.put("callback_url", paystackConfig.getCallbackUrl());
        body.put("channels", List.of("card", "bank", "bank_transfer"));

        // Add metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("cart_reference", cart.getCartReference());
        metadata.put("cart_id", cart.getId());
        metadata.put("user_id", cart.getUserId());
        metadata.put("item_count", cart.getItemCount());
        
        if (request.getMetadata() != null) {
            metadata.putAll(request.getMetadata());
        }
        body.put("metadata", metadata);

        log.debug("Initializing Paystack payment with body: {}", body);

        try {
            // Call Paystack API
            Map<String, Object> response = paystackHttpClient.post(
                    "/transaction/initialize",
                    body,
                    Map.class
            );

            log.info("Paystack response: {}", response);

            if (response == null || !response.containsKey("data")) {
                log.error("Invalid response from Paystack");
                throw new PaymentException("Invalid response from Paystack initialize");
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            String authorizationUrl = data.get("authorization_url") != null 
                    ? String.valueOf(data.get("authorization_url")) : null;
            String accessCode = data.get("access_code") != null 
                    ? String.valueOf(data.get("access_code")) : null;

            log.info("Payment initialization successful for reference: {}", cart.getPaymentReference());

            return new InitPaymentResponse(
                    cart.getPaymentReference(),
                    authorizationUrl,
                    accessCode
            );

        } catch (Exception e) {
            log.error("Failed to initialize payment: {}", e.getMessage(), e);
            throw new PaymentException("Failed to initialize payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public GenericResponse verifyPayment(String reference) {
        log.info("Verifying payment for reference: {}", reference);

        try {
            VerifyResponseDto verifyResponseDto = paystackHttpClient.get(
                    "/transaction/verify/" + reference,
                    VerifyResponseDto.class
            );

            if (verifyResponseDto == null || !verifyResponseDto.isStatus() || 
                verifyResponseDto.getData() == null) {
                log.warn("Verification failed for reference: {}", reference);
                return new GenericResponse(
                        false,
                        "Verification failed or invalid response",
                        HttpStatus.BAD_REQUEST,
                        verifyResponseDto
                );
            }

            VerifyResponseDto.Data data = verifyResponseDto.getData();
            log.info("Payment verification data: {}", data);

            // Find cart by payment reference
            Cart cart = cartRepository.findByPaymentReference(reference)
                    .orElseThrow(() -> new PaymentException(
                            "Cart not found for payment reference: " + reference));

            // Process payment based on status
            if ("success".equalsIgnoreCase(data.getStatus())) {
                processSuccessfulPayment(cart, data);
            } else {
                processFailedPayment(cart, data);
            }

            log.info("Verification successful for reference: {}, status: {}", 
                    reference, data.getStatus());

            return new GenericResponse(
                    true,
                    "Verification successful",
                    HttpStatus.OK,
                    verifyResponseDto
            );

        } catch (Exception ex) {
            log.error("Error verifying payment for reference: {}", reference, ex);
            throw new PaymentException("Error while verifying payment: " + ex.getMessage());
        }
    }

    @Override
    public boolean isSignatureValid(String rawBody, String signatureHeader) {
        try {
            String expectedSignature = generateHmacSha512(
                    rawBody,
                    paystackConfig.getWebhookSecret()
            );
            return expectedSignature.equals(signatureHeader);
        } catch (Exception e) {
            log.error("Error validating webhook signature", e);
            return false;
        }
    }

    @Async
    @Override
    public void processWebhookAsync(String rawBody) {
        try {
            Map<String, Object> payload = objectMapper.readValue(rawBody, Map.class);
            String event = (String) payload.get("event");
            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            if (data == null) {
                log.error("Webhook payload missing data field");
                return;
            }

            String reference = (String) data.get("reference");
            if (reference == null) {
                log.error("Webhook payload missing reference");
                return;
            }

            log.info("Processing payment webhook async for reference: {}, event: {}", 
                    reference, event);

            // Process based on event type
            if ("charge.success".equals(event)) {
                handleSuccessfulCharge(reference, data);
            } else {
                log.debug("Unhandled webhook event: {}", event);
            }

        } catch (Exception ex) {
            log.error("Failed to process webhook async: {}", ex.getMessage(), ex);
        }
    }

    // PRIVATE HELPER METHODS

    @Transactional
    protected void processSuccessfulPayment(Cart cart, VerifyResponseDto.Data data) {
        log.info("Processing successful payment for cart: {}", cart.getCartReference());

        // Update cart status
        cart.setStatus(CartStatus.COMPLETED);
        cartRepository.save(cart);

        // Update all orders in cart
        cart.getOrders().forEach(order -> {
            order.setStatus(OrderStatus.PAID);
            order.setAction(OrderAction.PENDING_REVIEW);
            order.setPaymentReference(cart.getPaymentReference());
            order.setIsInCart(false);
        });
        orderRepository.saveAll(cart.getOrders());

        log.info("Successfully processed payment for {} orders", cart.getOrders().size());
    }

    @Transactional
    protected void processFailedPayment(Cart cart, VerifyResponseDto.Data data) {
        log.warn("Processing failed payment for cart: {}", cart.getCartReference());

        // Update orders to failed status
        cart.getOrders().forEach(order -> {
            order.setStatus(OrderStatus.FAILED);
            order.setPaymentReference(cart.getPaymentReference());
        });
        orderRepository.saveAll(cart.getOrders());

        log.info("Marked {} orders as failed", cart.getOrders().size());
    }

    private void handleSuccessfulCharge(String reference, Map<String, Object> data) {
        try {
            Cart cart = cartRepository.findByPaymentReference(reference)
                    .orElseThrow(() -> new PaymentException(
                            "Cart not found for reference: " + reference));

            VerifyResponseDto.Data verificationData = objectMapper.convertValue(
                    data,
                    VerifyResponseDto.Data.class
            );

            processSuccessfulPayment(cart, verificationData);
        } catch (Exception e) {
            log.error("Failed to handle successful charge for reference: {}", reference, e);
        }
    }

    private String generateHmacSha512(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                "HmacSHA512"
        );
        mac.init(secretKeySpec);

        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private String serializeSafely(Object obj) {
        try {
            return obj == null ? null : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize object, returning empty JSON");
            return "{}";
        }
    }
}