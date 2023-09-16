package com.example.meetings.user.model.dto;

import com.example.meetings.user.model.domain.Role;

import java.util.List;

/**
 * Проекция для информации об аутентифицированном пользователе
 */
public interface AuthUserView {
    Long getUserId();
    String getUsername();
    String getFirstName();
    String getLastName();
    List<PhotoView> getPhotos();
    String getEmail();
    Integer getBalance();
    List<Role> getRoles();
    boolean isStatus();
    boolean isOnline();
}
