package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.payment.InitPaymentRequest;
import com.kudiapp.kudiapp.dto.request.payment.InitPaymentResponse;
import com.kudiapp.kudiapp.services.productService.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentsController {

    private final PaymentService paymentService;

    public PaymentsController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiatePayment")
    public ResponseEntity<GenericResponse> initPayment(@RequestBody InitPaymentRequest initPaymentRequest) {
        InitPaymentResponse resp = paymentService.initializePayment(initPaymentRequest);
        GenericResponse genericResponse = new GenericResponse(true, "Payment initialized", HttpStatus.CREATED, resp);
        return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @GetMapping("/verify/{reference}")
    public ResponseEntity<GenericResponse> verifyPayment(@PathVariable String reference) {
        GenericResponse response = paymentService.verifyPayment(reference);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping(value = "/webhook", consumes = "application/json")
    public ResponseEntity<String> handlePaystackWebhook(
            HttpServletRequest request,
            @RequestHeader(value = "Stripe-Signature", required = false) String signatureHeader) throws IOException {

        log.info("=== WEBHOOK REQUEST RECEIVED ===");
        log.info("Remote Address: {}", request.getRemoteAddr());
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Method: {}", request.getMethod());
        log.info("Content-Type: {}", request.getContentType());
        log.info("Signature Header: {}", signatureHeader);
        // Get raw body directly from request
        String rawBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        log.info("Raw Body length: {}", rawBody.length());
        log.info("Body preview: {}", rawBody.substring(0, Math.min(200, rawBody.length())));
        if (signatureHeader == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature");
        }

        if (!paymentService.isSignatureValid(rawBody, signatureHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature");
        }

        paymentService.processWebhookAsync(rawBody);
        return ResponseEntity.ok("Webhook received");
    }
}
