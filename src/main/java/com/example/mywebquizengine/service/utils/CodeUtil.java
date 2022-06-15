package com.example.mywebquizengine.service.utils;

import java.util.Random;

public class CodeUtil {
    public static String generate() {
        Random random = new Random();
        int rage = 9999;
        int code = 1000 + random.nextInt(rage - 1000);
        return String.valueOf(code);
    }
}
