package com.example.mywebquizengine.auth.service;

import com.example.mywebquizengine.auth.service.AuthService;
import com.example.mywebquizengine.common.exception.CodeExpiredException;
import com.example.mywebquizengine.common.exception.GlobalErrorCode;
import com.example.mywebquizengine.common.exception.WrongChangePasswordCodeException;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Service
public class SignInCodeService {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    public void checkCodeExpire(String username) {
        User user = authService.findUserByUsername(username);
        if (user.getSignInViaPhoneCodeExpiration() == null) {
            return;
        }

        if (new GregorianCalendar().after(user.getSignInViaPhoneCodeExpiration())) {
            throw new CodeExpiredException("exception.code.expired", GlobalErrorCode.ERROR_CODE_EXPIRED);
        }
    }

    /**
     * Проверка кода для смены пароля заключается в нахождении записи в БД
     * с указанным именем пользователя и коду для смены пароля
     *
     * @param username           - имя пользователя
     * @param changePasswordCode - код для смены пароля
     */
    public void verifyChangePasswordCode(String username, String changePasswordCode) {
        boolean exists = userRepository.existsByChangePasswordCodeAndUsername(
                changePasswordCode,
                username
        );

        if (!exists) {
            throw new WrongChangePasswordCodeException(
                    "exception.wrong.change.password.code",
                    GlobalErrorCode.ERROR_WRONG_CHANGE_PASSWORD_CODE
            );
        }
    }
}
