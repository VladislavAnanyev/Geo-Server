package com.example.meetings.user.service;

import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.photo.model.domain.Photo;
import com.example.meetings.user.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.example.meetings.common.utils.Const.DEFAULT_PHOTO;

@Component
public class UserFactory {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User create(RegistrationModel registrationModel) {
        User user = new User();
        user.setUsername(registrationModel.getPhoneNumber());
        user.setFirstName(registrationModel.getFirstName());
        user.setPassword(passwordEncoder.encode(registrationModel.getPassword()));
        user.setStatus(true);
        user.setMainPhoto(
                new Photo()
                        .setUser(user)
                        .setPosition(0)
                        .setUrl(DEFAULT_PHOTO)
        );
        user.setPhotos(Collections.singletonList(user.getMainPhoto()));
        user.setSignInViaPhoneCodeExpiration(LocalDateTime.now().plusMinutes(3));
        user.setOnline(false);

        return user;
    }
}
