package com.example.meetings.integration.moderation;

import org.springframework.lang.NonNull;

/**
 * Клиент LLM для модерации текста (Ollama, Yandex Alice и т.д.).
 */
public interface ModerationLlmClient {

    /**
     * @param prompt полный текст промпта
     * @return сырой текст ответа модели или {@code null}, если тело ответа пустое
     */
    String generate(@NonNull String prompt);
}
