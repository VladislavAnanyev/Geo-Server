package com.example.meetings.auth.facade;

import com.example.meetings.auth.model.dto.input.AuthRequest;
import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.auth.model.dto.output.AuthResult;

/**
 * Фасад сценариев аутентификации и входа в приложение.
 */
public interface AuthFacade {

    /**
     * Выполняет вход по учётным данным и возвращает токены доступа.
     *
     * @param authRequest логин, пароль и FCM-токен устройства
     * @return идентификатор пользователя, JWT, refresh-токен, очередь RabbitMQ и роли
     */
    AuthResult signIn(AuthRequest authRequest);

    /**
     * Запускает вход по номеру телефона: регистрация нового пользователя или отправка одноразового кода.
     *
     * @param phone номер телефона пользователя
     */
    void signInViaPhone(String phone);

    /**
     * Запускает вход по email: регистрация нового пользователя или отправка одноразового кода.
     *
     * @param email адрес электронной почты пользователя
     */
    void signInViaEmail(String email);

    /**
     * Обновляет JWT по действующему refresh-токену.
     *
     * @param refreshToken текущий refresh-токен пользователя
     * @return новый JWT, обновлённый refresh-токен, идентификатор пользователя и роли
     */
    AuthResult getNewAccessToken(String refreshToken);
}
