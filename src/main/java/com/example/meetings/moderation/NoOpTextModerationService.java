package com.example.meetings.moderation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Заглушка модерации текста, когда LLM-модерация отключена.
 */
@Service
@ConditionalOnProperty(name = "text.moderation.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpTextModerationService implements TextModerationService {

    /**
     * Не выполняет проверку текста поля профиля.
     *
     * @param fieldLabel название поля
     * @param text       проверяемый текст
     */
    @Override
    public void validateUserProfileText(String fieldLabel, String text) {
    }

    /**
     * Не выполняет проверку текста плана.
     *
     * @param title       заголовок плана
     * @param description описание плана
     */
    @Override
    public void validatePlanText(String title, String description) {
    }
}
