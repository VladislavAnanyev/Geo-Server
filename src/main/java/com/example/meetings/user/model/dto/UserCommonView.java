package com.example.meetings.user.model.dto;

import com.example.meetings.user.model.domain.Gender;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

/**
 * Проекция для информации о случайном пользователе вне его профиля
 */
public interface UserCommonView {
    Long getUserId();

    String getUsername();

    String getFirstName();

    Gender getGender();

    LocalDate getBirthDate();

    @Value("#{target.mainPhoto.url}")
    String getAvatar();

    boolean getOnline();
}

