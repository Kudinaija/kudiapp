package com.kudiapp.kudiapp.enums.productService;

import lombok.Getter;

@Getter
public enum ServiceProductPlanType {
    DEFAULT("Default"),
    PREMIUM("Premium"),
    BASIC("Basic"),
    CUSTOM("Custom");

    private final String displayName;

    ServiceProductPlanType(String displayName) {
        this.displayName = displayName;
    }
}
