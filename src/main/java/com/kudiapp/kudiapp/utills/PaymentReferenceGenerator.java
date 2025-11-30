package com.kudiapp.kudiapp.utills;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class PaymentReferenceGenerator {
    public static String generateReference() {
        String prefix = "KUDI25-";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999); // 4 digits
        return prefix + timestamp + randomNum;
    }
}
