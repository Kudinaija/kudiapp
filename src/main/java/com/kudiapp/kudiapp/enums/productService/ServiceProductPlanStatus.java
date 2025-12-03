package com.kudiapp.kudiapp.enums.productService;

import lombok.Getter;

@Getter
public enum ServiceProductPlanStatus {
    ACTIVE("Active"),
    DEACTIVATED("Deactivated"),
    SUSPENDED("Suspended"),
    PENDING_ACTIVATION("Pending Activation");

    private final String displayName;

    ServiceProductPlanStatus(String displayName) {
        this.displayName = displayName;
    }
}