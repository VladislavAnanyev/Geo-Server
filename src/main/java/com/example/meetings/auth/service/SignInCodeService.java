package com.example.meetings.auth.service;

import com.example.meetings.common.exception.CodeExpiredException;
import com.example.meetings.common.exception.GlobalErrorCode;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

        if (LocalDateTime.now().isAfter(user.getSignInViaPhoneCodeExpiration())) {
            throw new CodeExpiredException("exception.code.expired", GlobalErrorCode.ERROR_CODE_EXPIRED);
        }
    }

}
