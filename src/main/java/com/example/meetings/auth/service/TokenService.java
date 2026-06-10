package com.example.meetings.auth.service;

/**
 * Создание JWT-токенов доступа для аутентифицированных пользователей.
 */
public interface TokenService {

    /**
     * Формирует JWT для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return строка JWT
     */
    String createToken(Long userId);
}
