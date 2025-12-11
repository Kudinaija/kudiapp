package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.InitPaymentRequest;
import com.kudiapp.kudiapp.dto.productService.InitPaymentResponse;
import com.kudiapp.kudiapp.services.productService.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * REST Controller for managing payments via Paystack
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "Endpoints for managing payments via Paystack")
public class PaymentsController {

    private final PaymentService paymentService;

    @PostMapping("/initialize")
    @Operation(
            summary = "Initialize payment",
            description = "Initializes a payment transaction with Paystack for a checked-out cart"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment initialized successfully",
                    content = @Content(schema = @Schema(implementation = InitPaymentResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    public ResponseEntity<GenericResponse> initializePayment(
            @RequestBody InitPaymentRequest request) {
        
        log.info("Payment initialization requested for cart: {}", request.getCartReference());
        
        InitPaymentResponse response = paymentService.initializePayment(request);
        
        GenericResponse genericResponse = new GenericResponse(
                true,
                "Payment initialized successfully",
                HttpStatus.CREATED,
                response
        );
        
        return new ResponseEntity<>(genericResponse, HttpStatus.CREATED);
    }

    @GetMapping("/verify/{reference}")
    @Operation(
            summary = "Verify payment",
            description = "Verifies a payment transaction with Paystack and updates order statuses"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment verification completed",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Payment reference not found")
    })
    public ResponseEntity<GenericResponse> verifyPayment(
            @Parameter(description = "Payment reference to verify", required = true)
            @PathVariable String reference) {
        
        log.info("Payment verification requested for reference: {}", reference);
        
        GenericResponse response = paymentService.verifyPayment(reference);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping(value = "/webhook", consumes = "application/json")
    @Operation(
            summary = "Paystack webhook handler",
            description = "Handles webhook notifications from Paystack for payment events"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook processed successfully"),
            @ApiResponse(responseCode = "403", description = "Invalid signature")
    })
    public ResponseEntity<String> handlePaystackWebhook(
            HttpServletRequest request,
            @RequestHeader(value = "x-paystack-signature", required = false) String signatureHeader) 
            throws IOException {

        log.info("=== WEBHOOK REQUEST RECEIVED ===");
        log.info("Remote Address: {}", request.getRemoteAddr());
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Method: {}", request.getMethod());
        log.info("Content-Type: {}", request.getContentType());
        log.info("Signature Header: {}", signatureHeader);

        // Get raw body from request
        String rawBody = request.getReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));

        log.info("Raw Body length: {}", rawBody.length());
        log.info("Body preview: {}", rawBody.substring(0, Math.min(200, rawBody.length())));

        // Validate signature
        if (signatureHeader == null) {
            log.warn("Webhook rejected: Missing signature header");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature");
        }

        if (!paymentService.isSignatureValid(rawBody, signatureHeader)) {
            log.warn("Webhook rejected: Invalid signature");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature");
        }

        log.info("Webhook signature validated successfully");

        // Process webhook asynchronously
        paymentService.processWebhookAsync(rawBody);

        return ResponseEntity.ok("Webhook received");
    }
}