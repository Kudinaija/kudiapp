package com.kudiapp.kudiapp.services.serviceImpl.productSeervice;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.payment.InitPaymentRequest;
import com.kudiapp.kudiapp.dto.request.payment.InitPaymentResponse;
import com.kudiapp.kudiapp.exceptions.FailedProcessException;
import com.kudiapp.kudiapp.services.productService.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentSearchResult;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentSearchParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret}")
    private String stripeWebhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * 1️⃣ Initialize Payment (Create PaymentIntent)
     */
    @Override
    public InitPaymentResponse initializePayment(InitPaymentRequest request) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", request.getAmount());
            params.put("currency", request.getCurrency());
            params.put("receipt_email", request.getEmail());

            params.put("metadata", Map.of(
                    "reference", request.getReference(),
                    "email", request.getEmail()
            ));

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            return new InitPaymentResponse(
                    paymentIntent.getId(),
                    paymentIntent.getClientSecret()
            );

        } catch (StripeException e) {
            log.error("Stripe init payment failed", e);
            throw new FailedProcessException("Unable to initialize payment");
        }
    }

    /**
     * 2️⃣ Verify Payment (Query Stripe)
     */
    @Override
    public GenericResponse verifyPayment(String reference) {
        try {
            PaymentIntentSearchParams params =
                    PaymentIntentSearchParams.builder()
                            .setQuery("metadata['reference']:'" + reference + "'")
                            .build();

            PaymentIntentSearchResult result =
                    PaymentIntent.search(params);

            if (result.getData().isEmpty()) {
                return new GenericResponse(
                        false,
                        "Payment not found",
                        HttpStatus.NOT_FOUND
                );
            }

            PaymentIntent intent = result.getData().get(0);

            if ("succeeded".equals(intent.getStatus())) {
                return new GenericResponse(
                        true,
                        "Payment successful",
                        HttpStatus.OK,
                        intent
                );
            }

            return new GenericResponse(
                    false,
                    "Payment not completed",
                    HttpStatus.BAD_REQUEST,
                    intent.getStatus()
            );

        } catch (StripeException e) {
            log.error("Error verifying payment", e);
            return new GenericResponse(
                    false,
                    "Verification failed",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * 3️⃣ Validate Stripe Webhook Signature
     */
    @Override
    public boolean isSignatureValid(String payload, String signatureHeader) {
        try {
            Webhook.constructEvent(
                    payload,
                    signatureHeader,
                    stripeWebhookSecret
            );
            return true;
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature", e);
            return false;
        }
    }

    /**
     * 4️⃣ Process Webhook (Async)
     */
    @Async
    @Override
    public void processWebhookAsync(String payload) {
        try {
            Event event = Event.GSON.fromJson(payload, Event.class);

            log.info("Stripe Event Type: {}", event.getType());

            switch (event.getType()) {

                case "payment_intent.succeeded" -> {
                    PaymentIntent intent =
                            (PaymentIntent) event.getDataObjectDeserializer()
                                    .getObject()
                                    .orElseThrow();

                    String reference = intent.getMetadata().get("reference");

                    log.info("Payment succeeded. Reference={}", reference);

                    // ✅ update DB
                    // ✅ mark transaction as SUCCESS
                }

                case "payment_intent.payment_failed" -> {
                    log.warn("Payment failed");
                }

                default -> log.info("Unhandled event type");
            }

        } catch (Exception e) {
            log.error("Webhook processing failed", e);
        }
    }
}
