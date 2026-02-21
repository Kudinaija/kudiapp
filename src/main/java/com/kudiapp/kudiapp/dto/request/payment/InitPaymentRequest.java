package com.kudiapp.kudiapp.dto.request.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InitPaymentRequest {

    private String email;
    private String reference;
    private String currency;
    private Long amount; // amount in main unit (e.g., NGN)

    // Optional: cart items if you want to support multiple
    private List<CartItem> items;

    // ✅ Add URLs for Checkout Session
    private String successUrl;
    private String cancelUrl;

    @Data
    public static class CartItem {
        private String name;
        private int quantity;
        private Long price; // smallest currency unit
    }
}