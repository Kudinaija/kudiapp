package com.kudiapp.kudiapp.enums;

import lombok.Getter;

@Getter
public enum PRODUCT_TITLE {
    JOURNAL_PUBLICATION("Journal you are to publish"),
    STANDARD_EXAMS("Standard exams payment"),
    PROFESSIONAL_CERTIFICATIONS("Professional certification payment"),
    UNIVERSITY_APPLICATION("University application processing"),
    BACKGROUND_CHECKS("Background verification service"),
    STREAMING_SUBSCRIPTIONS("Payment for streaming subscription"),
    IN_GAME_PURCHASE("Purchase inside a game"),
    MUSIC_DISTRIBUTION("Music distribution service"),
    AMAZON_ECOMMERCE("Amazon ecommerce related payment"),
    TECH_DESIGN_TOOLS("Technical or design tool payment"),
    CLOUD_SERVICES("Cloud service subscription payment"),
    DOMAINS_HOSTING("Domain and hosting payment"),
    DEVELOPER_ACCOUNT("Developer account subscription"),
    DIGITAL_ADVERTISING("Digital ads or promotion payment"),
    FREELANCE_PLATFORMS("Payment for freelance platforms"),
    PROFESSIONAL_MEMBERSHIPS("Membership for professional bodies"),
    JOB_BOARD_LISTING("Job board listing or payment"),
    AIRLINE_BUS_TICKETS("Airline or bus ticket payment"),
    HOTEL_BOOKINGS("Hotel booking payment"),
    CAR_RENTALS("Car rental payment"),
    TRAVEL_INSURANCE("Travel insurance payment"),
    VISA_EMBASSY_FEES("Visa or embassy processing fees"),
    EVENT_TICKETS("Event ticket purchase"),
    PRIVATE_INVOICES("Private invoice settlement"),
    MEDICAL_PAYMENTS("Medical or hospital payment"),
    AUCTION_PLATFORMS("Auction platform related payment"),
    DISCOUNTED_PHONE_PRE_ORDERS("Discounted phone preorder payment"),
    LINKEDIN_PREMIUM("LinkedIn premium subscription");

    private final String description;

    PRODUCT_TITLE(String description) {
        this.description = description;
    }
}
