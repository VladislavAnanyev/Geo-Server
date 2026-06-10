package com.example.meetings.auth.service;

/**
 * Генерация одноразовых кодов для входа и подтверждения по телефону.
 */
public interface CodeGenerationService {

    /**
     * Создаёт новый одноразовый код.
     *
     * @return сгенерированный код
     */
    String generate();
}
