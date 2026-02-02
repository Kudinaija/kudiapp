//package com.kudiapp.kudiapp.services;
//
//import com.kudiapp.kudiapp.dto.GenericResponse;
//import com.kudiapp.kudiapp.dto.productService.InitPaymentResponse;
//import com.kudiapp.kudiapp.dto.request.payment.InitPaymentRequest;
//import org.springframework.stereotype.Component;
//
//@Component
//public interface PaymentService {
//
//    InitPaymentResponse initializePayment(InitPaymentRequest request);
//
//    GenericResponse verifyPayment(String reference);
//
//    boolean isSignatureValid(String rawBody, String signatureHeader);
//    void processWebhookAsync(String rawBody);
//
//}