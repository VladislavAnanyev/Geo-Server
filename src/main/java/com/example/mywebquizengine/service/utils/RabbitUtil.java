package com.example.mywebquizengine.service.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RabbitUtil {

    private static final String salt = "$1$Zyi$3";

    public static String getExchangeName(String username) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(username.getBytes(StandardCharsets.UTF_8));
        messageDigest.update(salt.getBytes());

        byte[] digest = messageDigest.digest();

        return DigestUtils.sha256Hex(digest);
    }

}
