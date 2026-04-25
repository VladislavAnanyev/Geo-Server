package com.example.meetings.user.model.dto;

import com.example.meetings.user.model.domain.Gender;

import java.time.LocalDate;
import java.util.List;

/**
 * Проекция для информации о профиле случайного пользователя
 */
public interface ProfileView {
    Long getUserId();

    String getUsername();

    String getFirstName();

    String getDescription();

    Gender getGender();

    LocalDate getBirthDate();

    List<PhotoView> getPhotos();

    String getOnline();
}
