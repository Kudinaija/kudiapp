package com.kudiapp.kudiapp.enums.productService;

import lombok.Getter;

@Getter
public enum CartStatus {
    ACTIVE("Active - Can be modified"),
    CHECKED_OUT("Checked out - Payment initiated"),
    COMPLETED("Completed - Payment successful"),
    ABANDONED("Abandoned - Inactive for long period"),
    EXPIRED("Expired - Exceeded time limit");

    private final String displayName;

    CartStatus(String displayName) {
        this.displayName = displayName;
    }

    public boolean canBeModified() {
        return this == ACTIVE;
    }

    public boolean isFinalized() {
        return this == CHECKED_OUT || this == COMPLETED;
    }
}