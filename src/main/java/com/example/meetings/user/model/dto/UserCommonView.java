package com.example.meetings.user.model.dto;

/**
 * Проекция для информации о случайном пользователе вне его профиля
 */
public interface UserCommonView {
    Long getUserId();
    String getUsername();
    String getFirstName();
    String getLastName();
    String getAvatar();
    String getOnline();
}

