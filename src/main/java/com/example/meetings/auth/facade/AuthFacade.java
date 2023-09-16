package com.example.meetings.auth.facade;

import com.example.meetings.auth.model.RegistrationType;
import com.example.meetings.auth.model.dto.input.AuthRequest;
import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.auth.model.dto.output.AuthPhoneResult;
import com.example.meetings.auth.model.dto.output.AuthResult;
import com.example.meetings.auth.model.dto.output.UserExistDto;
import com.example.meetings.common.model.Client;
import org.springframework.stereotype.Service;

@Service
public interface AuthFacade {
    AuthResult signUp(RegistrationModel registrationModel, RegistrationType type);
    AuthResult signIn(AuthRequest authRequest);
    AuthResult signInViaExternalService(Object token);
    void createAndSendChangePasswordCodeToUser(String username, Client client);
    void updatePassword(String username, String code, String password);
    AuthPhoneResult signUpViaPhone(RegistrationModel registrationModel);
    AuthPhoneResult createAndSendOneTimePassword(String phone);
    UserExistDto checkForExistUser(String username);
    void verifyChangePasswordCode(String username, String changePasswordCode);
    AuthResult getNewAccessToken(String refreshToken);
}
