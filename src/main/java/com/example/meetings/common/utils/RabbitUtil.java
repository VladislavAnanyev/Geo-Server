package com.example.meetings.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Класс для получения обмена пользователя
 * Он генерируется на основе идентификатора пользователя при помощи соли (всегда одинаковый)
 * в следствии чего его можно не хранить в БД
 */
@Component
public class RabbitUtil {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    private static final String salt = "$1$Zyi$3";

    public String createExchange(Long userId) {
        String exchangeName = RabbitUtil.getExchangeName(userId);
        rabbitAdmin.declareExchange(
                new FanoutExchange(
                        exchangeName,
                        true,
                        false
                )
        );
        return exchangeName;
    }

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
