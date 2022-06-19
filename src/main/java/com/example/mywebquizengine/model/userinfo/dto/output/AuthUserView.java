package com.example.mywebquizengine.model.userinfo.dto.output;

import com.example.mywebquizengine.model.userinfo.domain.Photo;
import com.example.mywebquizengine.model.userinfo.domain.Role;

import java.util.List;

/**
 * Проекция для информации об аутентифицированном пользователе
 */
public interface AuthUserView {
    Long getUserId();
    String getUsername();
    String getFirstName();
    String getLastName();
    List<Photo> getPhotos();
    String getEmail();
    Integer getBalance();
    List<Role> getRoles();
    boolean isStatus();
    boolean isOnline();
}
