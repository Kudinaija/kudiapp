package com.kudiapp.kudiapp.enums.productService;

import lombok.Getter;

@Getter
public enum OrderAction {
    PENDING_REVIEW("Pending Admin Review"),
    IN_PROGRESS("Admin Processing"),
    COMPLETED("Admin Completed"),
    REJECTED("Rejected by Admin"),
    REQUIRES_INFO("Requires Additional Info");

    private final String displayName;

    OrderAction(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActionable() {
        return this == PENDING_REVIEW || this == IN_PROGRESS || this == REQUIRES_INFO;
    }
}