package com.example.mywebquizengine.model.userinfo.dto.output;

import com.example.mywebquizengine.model.userinfo.domain.Photo;

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
