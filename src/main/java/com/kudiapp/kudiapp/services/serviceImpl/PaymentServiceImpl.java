package com.kudiapp.kudiapp.services.serviceImpl;//
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kudiapp.kudiapp.config.PaystackHttpClient;
import com.kudiapp.kudiapp.config.paystack.PaystackConfig;
import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.InitPaymentResponse;
import com.kudiapp.kudiapp.dto.request.payment.InitPaymentRequest;
import com.kudiapp.kudiapp.dto.request.payment.VerifyResponseDto;
import com.kudiapp.kudiapp.enums.PaymentStatus;
import com.kudiapp.kudiapp.exceptions.PaymentException;
import com.kudiapp.kudiapp.models.Payment;
import com.kudiapp.kudiapp.repository.PaymentRepository;
import com.kudiapp.kudiapp.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${paystack.secret-key}")
    private String paystackSecretKey;

    @Value("${paystack.callback.url}")
    private String callbackUrl;

    private final RestTemplate restTemplate;

    private final PaystackHttpClient paystackHttpClient;
//    private final PaymentProcessor paymentProcessor;
    private final PaystackConfig paystackConfig;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentServiceImpl(RestTemplate restTemplate, PaystackHttpClient paystackHttpClient,
                              PaystackConfig paystackConfig,
                              PaymentRepository paymentRepository) {
        this.restTemplate = restTemplate;
        this.paystackHttpClient = paystackHttpClient;
        this.paystackConfig = paystackConfig;
        this.paymentRepository = paymentRepository;
    }
//
//    @Override
//    @Transactional
//    public InitPaymentResponse initializePayment(InitPaymentRequest request) {
//        log.info("Initializing payment for productId={}", request.getProductId());
//
//        if (request.getEmail() == null || request.getEmail().isBlank()) {
//            log.error("Initialization failed: email is missing. getProductId={}", request.getProductId());
//            throw new PaymentException("Email is required");
//        }

    @Override
    public InitPaymentResponse initializePayment(InitPaymentRequest request) {
        return null;
    }

    @Override
    public GenericResponse verifyPayment(String reference) {
        return null;
    }

    ////        Booking booking = bookingService.getBookingByBookingId(request.getBookingId());
////        log.info("Booking {}", booking);
////
////        BigDecimal amount = checkAmountToPay(
////                booking.getTotalAmount(),
////                booking.getBaseAmount(),
////                booking.getNumberOfTravelers(),
////                booking.getPaymentMethod()
////        );
//
//        long amountInKobo = amount.multiply(BigDecimal.valueOf(100)).longValueExact();
//
//        String reference = PaymentReferenceGenerator.generateReference();
//        log.debug("Generated payment reference={} for bookingReference={}", reference, booking.getBookingReference());
//
//        Payment payment = Payment.builder()
//                .reference(reference)
//                .bookingReference(booking.getBookingReference())
//                .amount(amount)
//                .currency(request.getCurrency() != null ? request.getCurrency() : "NGN")
//                .email(request.getEmail())
//                .status(PaymentStatus.PENDING)
//                .channel(request.getChannel() != null ? request.getChannel() : "card")
//                .createdAt(Instant.now())
//                .metadataJson(serializeSafely(request.getMetadata()))
//                .build();
//
//        paymentRepository.save(payment);
//        log.info("Payment record created with reference={} and status={}", reference, PaymentStatus.PENDING);
//
//        List<String> allowedChannels = List.of("bank", "card", "bank_transfer");
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("email", request.getEmail());
//        body.put("amount", amountInKobo);
//        body.put("currency", request.getCurrency() != null ? request.getCurrency() : "NGN");
//        body.put("reference", reference);
//        body.put("channels", allowedChannels);
//
//        if (request.getMetadata() != null && !request.getMetadata().isEmpty()) {
//            log.debug("Metadata provided for payment reference={}", reference);
//            body.put("metadata", request.getMetadata());
//        }
//
//        Map response = paystackHttpClient.post("/transaction/initialize", body, Map.class)
//                .onErrorMap(err -> {
//                    log.error("Error calling Paystack initialize for reference={}: {}", reference, err.getMessage());
//                    return new PaymentException("error calling paystack initialize: " + err);
//                })
//                .block();
//
//        log.info("The response {} from paystack for initializing payment {}", response, body);
//
//        if (response == null || !response.containsKey("data")) {
//            log.error("Invalid response from Paystack for reference={}", reference);
//            throw new PaymentException("Invalid response from Paystack initialize");
//        }
//
//        Map data = (Map) response.get("data");
//        String authorizationUrl = data.get("authorization_url") != null ? String.valueOf(data.get("authorization_url")) : null;
//        String accessCode = data.get("access_code") != null ? String.valueOf(data.get("access_code")) : null;
//
//        payment.setAuthorizationUrl(authorizationUrl);
//        payment.setAccessCode(accessCode);
//        paymentRepository.save(payment);
//
//        log.info("Payment initialization successful for reference={}. Authorization URL and Access Code set.", reference);
//
//        return new InitPaymentResponse(reference, authorizationUrl, accessCode);
//    }

//    @Override
//    @Transactional
//    public GenericResponse verifyPayment(String reference) {
//        log.info("Verifying payment for reference={}", reference);
//        try {
//            VerifyResponseDto verifyResponseDto = paystackHttpClient
//                    .get("/transaction/verify/" + reference, VerifyResponseDto.class)
//                    .block();
//
//            if (verifyResponseDto == null || !verifyResponseDto.isStatus() || verifyResponseDto.getData() == null) {
//                log.warn("Verification failed for reference={}. Invalid or failed response.", reference);
//                return new GenericResponse(false, "Verification failed or invalid response", HttpStatus.BAD_REQUEST, verifyResponseDto);
//            }
//
//            VerifyResponseDto.Data data = verifyResponseDto.getData();
//            log.info("The data {} from paystack for verify payment {}", data, verifyResponseDto);
//
//            Payment payment = paymentRepository.findByReference(reference)
//                    .orElseThrow(() -> {
//                        log.error("Payment record not found for reference={}", reference);
//                        return new PaymentException("Payment record not found for reference: " + reference);
//                    });
//
//            mapPayment(payment, data);
//            log.info("Verification successful for reference={}, status={}", reference, data.getStatus());
//
//            return new GenericResponse(true, "Verification successful", HttpStatus.OK, verifyResponseDto);
//        } catch (Exception ex) {
//            log.error("Error verifying payment for reference={}", reference, ex);
//            throw new PaymentException("Error while verifying payment: " + ex.getMessage());
//        }
//    }

    public boolean isSignatureValid(String rawBody, String signatureHeader) {
        try {
            String expectedSignature = generateHmacSha512(rawBody, paystackSecretKey);
            return expectedSignature.equals(signatureHeader);
        } catch (Exception e) {
            log.error("Error validating webhook signature", e);
            return false;
        }
    }

    @Override
    public void processWebhookAsync(String rawBody) {

    }

    private String generateHmacSha512(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
        mac.init(secretKeySpec);

        byte[] hash = mac.doFinal(data.getBytes());

        // Convert to hex string
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
//
//    @Async
//    @Override
//    public void processWebhookAsync(String rawBody) {
//        try {
//            Map payload = objectMapper.readValue(rawBody, Map.class);
//            Map data = (Map) payload.get("data");
//
//            if (data == null) {
//                log.error("Webhook payload missing data field: {}", payload);
//                return;
//            }
//
//            String reference = (String) data.get("reference");
//            if (reference == null) {
//                log.error("Webhook payload missing reference");
//                return;
//            }
//
//            log.info("Processing payment webhook async for reference={}", reference);
//
//            // Decide based on prefix
//            if (reference.startsWith("HOSPAY025-")) {
//                // Process locally
//                log.info("Reference belongs to hospitality app. Handling internally.");
//                paymentProcessor.handlePayment(reference, data);
//
//            } else
////                if (reference.startsWith("HANPRO"))
//                {
//                // Forward to another app
//                log.info("Reference belongs to external app. Forwarding...");
////                forwardToOtherApp(payload);
//            }
////            else {
////                log.warn("Unknown reference prefix. Skipping: {}", reference);
////            }
//
//        } catch (Exception ex) {
//            log.error("Failed to process webhook async: {}", ex.getMessage(), ex);
//        }
//    }

    private String serializeSafely(Object obj) {
        try {
            return obj == null ? null : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize metadata, returning empty JSON");
            return "{}";
        }
    }

    private String computeHmacSha512(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] macBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : macBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Failed to compute HMAC");
            throw new PaymentException("Failed to compute HMAC");
        }
    }

    private Payment mapPayment(Payment payment, VerifyResponseDto.Data data) {
        log.debug("Mapping Paystack response to payment reference={}", payment.getReference());

        payment.setGatewayResponse(data.getGatewayResponse());
        payment.setChannel(data.getChannel());

        if (data.getAmount() != null) {
            log.info("Setting amount for reference={} with raw amount={}", payment.getReference(), data.getAmount());
            payment.setAmount(BigDecimal.valueOf(data.getAmount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        } else {
            log.warn("Amount is null for payment reference={}", payment.getReference());
        }

        String paystackStatus = data.getStatus() != null ? data.getStatus() : "";
        PaymentStatus status = switch (paystackStatus.toLowerCase()) {
            case "success" -> PaymentStatus.SUCCESS;
            case "failed" -> PaymentStatus.FAILED;
            default -> PaymentStatus.PENDING;
        };
        payment.setStatus(status);
        log.info("Mapped status={} for reference={}", status, payment.getReference());

        if (status == PaymentStatus.SUCCESS) {
            payment.setPaidAt(Instant.now());
            log.info("PaidAt timestamp set for reference={}", payment.getReference());
        }

        return payment;
    }

//    public BigDecimal checkAmountToPay(BigDecimal bookingTotalPrice, BigDecimal baseAmount, Integer numberOfTravelers, PaymentMethod type) {
//        BigDecimal amount;
//
//        // common calculation for down payment variants
//        BigDecimal downPayment = baseAmount.add(baseAmount.multiply(BigDecimal.valueOf(numberOfTravelers-1)));
//
//        switch (type) {
//            case ONE_OFF:
//                amount = bookingTotalPrice;
//                break;
//
//            case INSTALLMENT:
//                amount = bookingTotalPrice.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
//                break;
//
//            case DOWN_PAYMENT:
//                if (bookingTotalPrice.compareTo(downPayment) <= 0) {
//                    throw new InvalidCredentialsException("Booking price must be greater than " + downPayment + " for down payment option.");
//                }
//                amount = downPayment;
//                break;
//
//            case DOWN_PAYMENT_ONE_OFF:
//                if (bookingTotalPrice.compareTo(downPayment) <= 0) {
//                    throw new InvalidCredentialsException("Booking price must be greater than " + downPayment + " for down payment one-off option.");
//                }
//                amount = downPayment;
//                break;
//
//            case DOWN_PAYMENT_INSTALLMENT:
//                if (bookingTotalPrice.compareTo(downPayment) <= 0) {
//                    throw new InvalidCredentialsException("Booking price must be greater than " + downPayment + " for down payment installment option.");
//                }
//                amount = downPayment.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
//                break;
//
//            default:
//                throw new InvalidCredentialsException("Unsupported payment type");
//        }
//
//        return amount;
//    }

//    private void forwardToOtherApp(Map payload) {
//        try {
//            log.info("Forwarding webhook to callback URL: {}", callbackUrl);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<Map> request = new HttpEntity<>(payload, headers);
//
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                    callbackUrl,
//                    request,
//                    String.class
//            );
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                log.info("Successfully forwarded webhook to external app. Status: {}", response.getStatusCode());
//            } else {
//                log.warn("Failed to forward webhook. Status: {}, Response: {}",
//                        response.getStatusCode(), response.getBody());
//            }
//
//        } catch (Exception ex) {
//            log.error("Error forwarding webhook to external app: {}", ex.getMessage(), ex);
//        }
//    }
}