package com.example.mywebquizengine.user.model.dto;

import com.example.mywebquizengine.photo.model.domain.Photo;

import java.util.List;

/**
 * Проекция для информации о профиле случайного пользователя
 */
public interface ProfileView {
    Long getUserId();
    String getUsername();
    String getFirstName();
    String getLastName();
    List<Photo> getPhotos();
    String getEmail();
    String getOnline();
}
