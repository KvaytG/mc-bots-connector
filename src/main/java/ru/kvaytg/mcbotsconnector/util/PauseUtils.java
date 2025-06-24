package ru.kvaytg.mcbotsconnector.util;

public class PauseUtils {

    private PauseUtils() {
        throw new AssertionError("No instances allowed");
    }

    public static void wait(int pauseTime) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < pauseTime) {}
    }

}