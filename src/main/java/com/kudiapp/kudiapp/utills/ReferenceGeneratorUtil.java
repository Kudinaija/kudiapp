package com.kudiapp.kudiapp.utills;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating unique reference numbers for orders, carts, and payments
 */
public class ReferenceGeneratorUtil {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Generate unique order reference
     * Format: ORD-YYYYMMDDHHMMSS-XXXX
     */
    public static String generateOrderReference() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD-" + timestamp + "-" + random;
    }

    /**
     * Generate unique cart reference
     * Format: CART-YYYYMMDDHHMMSS-XXXX
     */
    public static String generateCartReference() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "CART-" + timestamp + "-" + random;
    }

    /**
     * Generate unique payment reference for Paystack
     * Format: KUDI-YYYYMMDDHHMMSS-XXXX
     */
    public static String generatePaymentReference() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "KUDI-" + timestamp + "-" + random;
    }
}