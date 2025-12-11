package com.kudiapp.kudiapp.enums.productService;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pending Payment"),
    PAID("Payment Confirmed"),
    PROCESSING("Being Processed"),
    COMPLETED("Completed Successfully"),
    CANCELLED("Cancelled"),
    REFUNDED("Payment Refunded"),
    FAILED("Payment Failed");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public boolean isPaid() {
        return this == PAID || this == PROCESSING || this == COMPLETED;
    }

    public boolean canBeCancelled() {
        return this == PENDING || this == PAID;
    }
}