package com.kudiapp.kudiapp.utills;

import java.util.concurrent.ThreadLocalRandom;

public class UserIdUtil {

//    public static String generateKudiUserId() {
//        long timestamp = System.currentTimeMillis();
//        int random8 = ThreadLocalRandom.current().nextInt(10000000, 99999999); // 8 digits
//
//        return "Kudi" + timestamp + random8;
//    }

    public static String generateKudiUserId() {
        long tsPart = System.currentTimeMillis() % 10_000_000_000L;
        int randomPart = ThreadLocalRandom.current().nextInt(100, 1000);

        return String.format("%010d%03d", tsPart, randomPart);
    }
}
