package com.example.meetings.user.model.dto;

import org.springframework.beans.factory.annotation.Value;

/**
 * Проекция для информации о случайном пользователе вне его профиля
 */
public interface UserCommonView {
    Long getUserId();

    String getUsername();

    String getFirstName();

    String getLastName();

    @Value("target.mainPhoto.url")
    String getAvatar();

    String getOnline();
}

