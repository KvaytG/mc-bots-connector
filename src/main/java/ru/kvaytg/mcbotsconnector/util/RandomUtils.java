package ru.kvaytg.mcbotsconnector.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    private RandomUtils() {
        throw new AssertionError("No instances allowed");
    }

    private static final char[] SYMBOLS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            builder.append(SYMBOLS[random.nextInt(SYMBOLS.length)]);
        }
        return builder.toString();
    }

}