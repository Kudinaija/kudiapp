package com.kudiapp.kudiapp.enums.productService;

import lombok.Getter;

@Getter
public enum ServiceProductStatus {
    DRAFT("Draft - Work in Progress"),
    ACTIVE("Active - Available for Users"),
    INACTIVE("Inactive - Temporarily Disabled"),
    ARCHIVED("Archived - No Longer Available");

    private final String displayName;

    ServiceProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public boolean isPubliclyVisible() {
        return this == ACTIVE;
    }

    public boolean canBeOrdered() {
        return this == ACTIVE;
    }
}
