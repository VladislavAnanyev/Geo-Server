package com.example.meetings.moderation;

/**
 * Сервис модерации пользовательского текста.
 */
public interface TextModerationService {

    /**
     * Проверяет текст поля профиля пользователя.
     *
     * @param fieldLabel название поля для сообщения об ошибке
     * @param text       проверяемый текст
     */
    void validateUserProfileText(String fieldLabel, String text);

    /**
     * Проверяет заголовок и описание плана.
     *
     * @param title       заголовок плана
     * @param description описание плана
     */
    void validatePlanText(String title, String description);
}
