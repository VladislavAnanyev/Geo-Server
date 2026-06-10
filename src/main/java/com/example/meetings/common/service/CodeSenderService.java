package com.example.meetings.common.service;

import org.springframework.stereotype.Service;

/**
 * Отправка одноразовых кодов подтверждения на телефон пользователя.
 */
@Service
public interface CodeSenderService {

    /**
     * Отправляет код подтверждения на указанный номер телефона.
     *
     * @param code  одноразовый код
     * @param phone номер телефона получателя
     */
    void sendCodeToPhone(String code, String phone);
}
