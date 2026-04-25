package com.example.meetings.user.model.dto;

import com.example.meetings.user.model.domain.Gender;

import java.time.LocalDate;
import java.util.List;

/**
 * Проекция для информации об аутентифицированном пользователе
 */
public interface AuthUserView {
    Long getUserId();

    String getUsername();

    String getFirstName();

    List<PhotoView> getPhotos();

    boolean isStatus();

    boolean isOnline();

    String getDescription();

    Gender getGender();

    LocalDate getBirthDate();
}
