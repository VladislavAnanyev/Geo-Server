package com.example.meetings.common.utils;

import java.util.Random;
import java.util.UUID;

public class CodeUtil {
    public static String generateShortCode() {
        Random random = new Random();
        int rage = 9999;
        int code = 1000 + random.nextInt(rage - 1000);
        return String.valueOf(code);
    }

    public static String generateLongCode() {
        return UUID.randomUUID().toString();
    }
}
