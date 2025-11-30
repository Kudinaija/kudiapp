package com.kudiapp.kudiapp.dto.request.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitPaymentRequest {
    private String email;
    private String currency;
    private String channel;
    private Long productId;
    private Map<String, Object> metadata;
}
