package com.kudiapp.kudiapp.services;

import com.kudiapp.kudiapp.dto.request.payment.VerifyResponseDto;
import com.kudiapp.kudiapp.models.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class ExternalNotifier {

    private final RestTemplate restTemplate = new RestTemplate();

    public void notifySourceApp(String reference, Payment payment, VerifyResponseDto.Data verificationData) {
        String callbackUrl;

        if (reference.startsWith("HOSPAY025-")) {
            callbackUrl = "https://hanpro-app.com/api/payments/webhook";
        } else if (reference.startsWith("APPB")) {
            callbackUrl = "https://app-b.com/api/payment/callback";
        } else {
            log.warn("Unknown source app for reference={}, skipping callback", reference);
            return;
        }

        Map<String, Object> payload = Map.of(
            "reference", reference,
            "status", payment.getStatus(),
            "amount", verificationData.getAmount(),
            "gatewayResponse", verificationData.getGatewayResponse()
        );

        try {
            restTemplate.postForEntity(callbackUrl, payload, Void.class);
            log.info("Callback sent to {} for reference={}", callbackUrl, reference);
        } catch (Exception e) {
            log.error("Failed to notify source app {} for reference={}: {}", callbackUrl, reference, e.getMessage());
        }
    }
}
