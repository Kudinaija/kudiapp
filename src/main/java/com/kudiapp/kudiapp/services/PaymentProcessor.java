//package com.kudiapp.kudiapp.services;//
//import com.snappapp.snapng.config.paystack.PaystackHttpClient;
//import com.snappapp.snapng.dto.request.payment.VerifyResponseDto;
//import com.snappapp.snapng.enums.PaymentStatus;
//import com.snappapp.snapng.exceptions.PaymentException;
//import com.snappapp.snapng.models.Payment;
//import com.snappapp.snapng.repository.PaymentRepository;
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.Instant;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class PaymentProcessor {
//
//    private final PaymentRepository paymentRepository;
//    private final PaystackHttpClient paystackHttpClient;
//    private final ExternalNotifier externalNotifier;
//
//    public PaymentProcessor(PaymentRepository paymentRepository, PaystackHttpClient paystackHttpClient, ExternalNotifier externalNotifier) {
//        this.paymentRepository = paymentRepository;
//        this.paystackHttpClient = paystackHttpClient;
//        this.externalNotifier = externalNotifier;
//    }
//
//    @Transactional
//    public void handlePayment(String reference, Map<String, Object> data) {
//        // Step 1: Verify with Paystack
//        VerifyResponseDto verifyResponseDto = paystackHttpClient
//                .get("/transaction/verify/" + reference, VerifyResponseDto.class)
//                .block();
//
//        if (verifyResponseDto == null || !verifyResponseDto.isStatus()) {
//            log.warn("Unable to verify transaction for reference={}", reference);
//            return;
//        }
//
//        VerifyResponseDto.Data verificationData = verifyResponseDto.getData();
//
//        // Step 2: Find and update Payment
//        Payment payment = paymentRepository.findByReference(reference)
//                .orElseThrow(() -> new PaymentException("Payment not found: " + reference));
//
//        if ("success".equalsIgnoreCase(verificationData.getStatus())) {
//            mapPayment(payment, verificationData);
////            bookingService.markBookingAsPaid(payment.getBookingReference(), payment);
////            payment.setStatus(PaymentStatus.SUCCESS);
//        } else {
////            payment.setStatus(PaymentStatus.FAILED);
//        }
//
//        paymentRepository.save(payment);
//
//        // Step 3: Route back to source app
//        externalNotifier.notifySourceApp(reference, payment, verificationData);
//    }
//
//    private Payment mapPayment(Payment payment, VerifyResponseDto.Data data) {
//        log.debug("Mapping Paystack response to payment reference={}", payment.getReference());
//
//        payment.setGatewayResponse(data.getGatewayResponse());
//        payment.setChannel(data.getChannel());
//
//        if (data.getAmount() != null) {
//            log.info("Setting amount for reference={} with raw amount={}", payment.getReference(), data.getAmount());
//            payment.setAmount(BigDecimal.valueOf(data.getAmount())
//                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
//        } else {
//            log.warn("Amount is null for payment reference={}", payment.getReference());
//        }
//        String paystackStatus = data.getStatus() != null ? data.getStatus() : "";
//        PaymentStatus status = switch (paystackStatus.toLowerCase()) {
//            case "success" -> PaymentStatus.SUCCESS;
//            case "failed" -> PaymentStatus.FAILED;
//            default -> PaymentStatus.PENDING;
//        };
//        payment.setStatus(status);
//        log.info("Mapped status={} for reference={}", status, payment.getReference());
//
//        if (status == PaymentStatus.SUCCESS) {
//            payment.setPaidAt(Instant.now());
//            log.info("PaidAt timestamp set for reference={}", payment.getReference());
//        }
//
//        return payment;
//    }
//
//}
