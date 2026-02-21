package com.kudiapp.kudiapp.dto.request.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InitPaymentResponse {
    private String paymentIntentId;
    private String clientSecret;
    private String url;
}
