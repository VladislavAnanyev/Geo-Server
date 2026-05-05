package com.example.meetings.auth.facade;

import com.example.meetings.auth.model.dto.input.AuthRequest;
import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.auth.model.dto.output.AuthResult;
import org.springframework.stereotype.Service;

@Service
public interface AuthFacade {
    AuthResult signIn(AuthRequest authRequest);

    void signInViaPhone(String phone);

    void signInViaEmail(String email);

    AuthResult getNewAccessToken(String refreshToken);
}
