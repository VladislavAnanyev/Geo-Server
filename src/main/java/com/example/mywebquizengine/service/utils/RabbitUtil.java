package com.example.mywebquizengine.service.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RabbitUtil {

    private static final String salt = "$1$Zyi$3";

    public static String getExchangeName(String username) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        messageDigest.update(username.getBytes(StandardCharsets.UTF_8));
        messageDigest.update(salt.getBytes());

        byte[] digest = messageDigest.digest();

        return DigestUtils.sha256Hex(digest);
    }

}
