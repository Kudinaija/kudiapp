package com.kudiapp.kudiapp.dto.productService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for initializing payment with Paystack
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitPaymentRequest {
    
    /**
     * Customer email address (required by Paystack)
     */
    private String email;
    
    /**
     * Cart reference for the payment
     */
    private String cartReference;
    
    /**
     * Optional metadata to attach to payment
     */
    private Map<String, Object> metadata;
}