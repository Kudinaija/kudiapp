package com.kudiapp.kudiapp.enums;

import lombok.Getter;

@Getter
public enum Category {
    ENTERTAINMENT_MEDIA("Entertainment & Media"),
    EDUCATION_TRAINING("Education & Training"),
    ONLINE_SHOPPING("Online Shopping"),
    TRAVEL_HOSPITALITY("Travel & Hospitality"),
    EVENTS_TICKETS("Events & Tickets"),
    OTHER_SERVICES("Other Services"),
    SPECIAL_OFFERS("Special Offers"),
    BUSINESS_PROFESSIONAL("Business & Professional");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }
}
