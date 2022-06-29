package com.example.mywebquizengine.auth;

import com.example.mywebquizengine.auth.model.RegistrationType;
import com.example.mywebquizengine.auth.model.dto.input.AuthRequest;
import com.example.mywebquizengine.auth.model.dto.input.RegistrationModel;
import com.example.mywebquizengine.auth.model.dto.output.AuthPhoneResponse;
import com.example.mywebquizengine.auth.model.dto.output.AuthResult;
import com.example.mywebquizengine.auth.model.dto.output.UserExistDto;
import com.example.mywebquizengine.common.common.Client;
import org.springframework.stereotype.Service;

@Service
public interface AuthFacade {

    AuthResult signUp(RegistrationModel registrationModel, RegistrationType type);
    AuthResult signIn(AuthRequest authRequest);
    AuthResult signInViaExternalService(Object token);
    void createAndSendChangePasswordCodeToUser(String username, Client client);
    void updatePassword(String username, String code, String password);
    AuthPhoneResponse signUpViaPhone(RegistrationModel registrationModel);
    AuthPhoneResponse createAndSendOneTimePassword(String phone);
    UserExistDto checkForExistUser(String username);
    void verifyChangePasswordCode(String username, String changePasswordCode);
}
