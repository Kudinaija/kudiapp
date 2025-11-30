package com.kudiapp.kudiapp.dto.request.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitPaymentResponse {
    private String reference;
    private String authorizationUrl;
    private String accessCode;
}
