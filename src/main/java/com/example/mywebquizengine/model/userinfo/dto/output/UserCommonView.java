package com.example.mywebquizengine.model.userinfo.dto.output;

import org.springframework.beans.factory.annotation.Value;

/**
 * Проекция для информации о случайном пользователе вне его профиля
 */
public interface UserCommonView {
    Long getUserId();
    String getUsername();
    String getFirstName();
    String getLastName();
    @Value("#{target.photos.get(0).url}")
    String getAvatar();
    String getOnline();
}

