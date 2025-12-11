package com.kudiapp.kudiapp.dto.productService;

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
