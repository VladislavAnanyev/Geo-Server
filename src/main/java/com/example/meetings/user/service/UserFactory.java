package com.example.meetings.user.service;

import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.user.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Фабрика создания сущности пользователя при регистрации.
 */
@Component
public class UserFactory {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Создаёт нового пользователя на основе данных регистрации.
     *
     * @param registrationModel модель регистрации с телефоном, паролем и именем
     * @return новая сущность пользователя, готовая к сохранению
     */
    public User create(RegistrationModel registrationModel) {
        User user = new User();
        user.setUsername(registrationModel.getPhoneNumber());
        user.setFirstName(registrationModel.getFirstName());
        user.setPassword(passwordEncoder.encode(registrationModel.getPassword()));
        user.setStatus(true);
        user.setSignInViaPhoneCodeExpiration(LocalDateTime.now().plusMinutes(3));
        user.setOnline(false);

        return user;
    }
}
