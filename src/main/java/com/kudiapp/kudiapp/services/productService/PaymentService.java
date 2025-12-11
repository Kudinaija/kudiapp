package com.kudiapp.kudiapp.services.productService;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.InitPaymentRequest;
import com.kudiapp.kudiapp.dto.productService.InitPaymentResponse;
import org.springframework.stereotype.Component;

@Component
public interface PaymentService {

    /**
     * Initialize a payment with Paystack
     * 
     * @param request Payment initialization request
     * @return Payment initialization response with authorization URL
     */
    InitPaymentResponse initializePayment(InitPaymentRequest request);

    /**
     * Verify a payment transaction with Paystack
     * 
     * @param reference Payment reference to verify
     * @return Generic response with verification details
     */
    GenericResponse verifyPayment(String reference);

    /**
     * Validate webhook signature from Paystack
     * 
     * @param rawBody Raw request body
     * @param signatureHeader Signature from Paystack
     * @return true if signature is valid
     */
    boolean isSignatureValid(String rawBody, String signatureHeader);

    /**
     * Process Paystack webhook asynchronously
     * 
     * @param rawBody Raw webhook body
     */
    void processWebhookAsync(String rawBody);
}