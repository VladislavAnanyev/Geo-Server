package com.example.meetings.auth.service;

import com.example.meetings.auth.model.UserToken;

/**
 * Создание и обновление refresh-токенов для аутентифицированных пользователей.
 */
public interface TokenService {

    /**
     * Создаёт refresh-токен для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return строка refresh-токена
     */
    String createToken(Long userId);

    /**
     * Обновляет refresh-токен: выдаёт новый идентификатор для существующей записи.
     *
     * @param oldRefreshToken текущий refresh-токен
     * @return обновлённая запись токена
     */
    UserToken updateRefreshToken(String oldRefreshToken);
}
