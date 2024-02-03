package com.example.meetings.user.model.dto;

import java.util.List;

/**
 * Проекция для информации о профиле случайного пользователя
 */
public interface ProfileView {
    Long getUserId();

    String getUsername();

    String getFirstName();

    String getLastName();

    List<PhotoView> getPhotos();

    String getEmail();

    String getOnline();
}
