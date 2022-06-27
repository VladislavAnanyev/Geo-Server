package com.example.mywebquizengine.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Класс для получения обмена пользователя
 * Он генерируется на основе идентификатора пользователя при помощи соли (всегда одинаковый)
 * в следствии чего его можно не хранить в БД
 */
public class RabbitUtil {

    private static final String salt = "$1$Zyi$3";

    public static String getExchangeName(Long userId) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        messageDigest.update(userId.toString().getBytes(StandardCharsets.UTF_8));
        messageDigest.update(salt.getBytes());

        byte[] digest = messageDigest.digest();

        return DigestUtils.sha256Hex(digest);
    }

}
