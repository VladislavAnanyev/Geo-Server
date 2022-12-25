package com.example.mywebquizengine.user.service;

import com.example.mywebquizengine.auth.model.dto.input.RegistrationModel;
import com.example.mywebquizengine.auth.model.RegistrationType;
import com.example.mywebquizengine.common.utils.CodeUtil;
import com.example.mywebquizengine.user.model.domain.Role;
import com.example.mywebquizengine.user.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

@Component
public class UserFactory {

    @Value("${hostname}")
    private String hostname;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User create(RegistrationModel registrationModel, RegistrationType type) {
        User user = new User();
        user.setUsername(registrationModel.getUsername());
        user.setFirstName(registrationModel.getFirstName());
        user.setLastName(registrationModel.getLastName());
        user.setEmail(registrationModel.getEmail());

        if (type.equals(RegistrationType.BASIC)) {
            user.setPassword(passwordEncoder.encode(registrationModel.getPassword()));
            user.setStatus(false);
            String avatar = hostname + "/img/default.jpg";
            user.setAvatar(avatar);
            user.setPhotos(Collections.singletonList(avatar));
            user.setActivationCode(CodeUtil.generateLongCode());
        } else if (type.equals(RegistrationType.OAUTH2)) {
            user.setAvatar(registrationModel.getAvatar());
            user.setPhotos(Collections.singletonList(registrationModel.getAvatar()));
            user.setStatus(true);
        } else if (type.equals(RegistrationType.PHONE)) {
            user.setPassword(passwordEncoder.encode(registrationModel.getPassword()));
            user.setStatus(true);
            String avatar = hostname + "/img/default.jpg";
            user.setAvatar(avatar);
            user.setPhotos(Collections.singletonList(avatar));

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.add(Calendar.MINUTE, 3);
            user.setSignInViaPhoneCodeExpiration(calendar);
        }

//        user.setEnabled(true);
        user.setBalance(0);
        user.grantAuthority(Role.ROLE_USER);
        user.setOnline(false);
        return user;
    }
}
