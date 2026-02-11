package com.kudiapp.kudiapp.services.productService;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.payment.InitPaymentRequest;
import com.kudiapp.kudiapp.dto.request.payment.InitPaymentResponse;

public interface PaymentService {

    InitPaymentResponse initializePayment(InitPaymentRequest request);

    GenericResponse verifyPayment(String reference);

    boolean isSignatureValid(String payload, String signatureHeader);

    void processWebhookAsync(String payload);
}
