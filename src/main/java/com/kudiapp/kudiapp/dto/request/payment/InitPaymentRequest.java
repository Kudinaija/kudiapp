package com.kudiapp.kudiapp.dto.request.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitPaymentRequest {
    private String email;                 // Customer email
    private String reference;             // Your internal transaction ref
    private BigDecimal amount;                  // amount in smallest unit (cents/kobo)
    private String currency;              // e.g. "usd" or "ngn"
    private Map<String, Object> metadata;
}
