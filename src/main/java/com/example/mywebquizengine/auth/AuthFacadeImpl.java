package com.example.mywebquizengine.auth;

import com.example.mywebquizengine.auth.model.RegistrationType;
import com.example.mywebquizengine.auth.model.dto.input.AuthRequest;
import com.example.mywebquizengine.auth.model.dto.input.RegistrationModel;
import com.example.mywebquizengine.auth.model.dto.output.AuthPhoneResponse;
import com.example.mywebquizengine.auth.model.dto.output.AuthResult;
import com.example.mywebquizengine.auth.model.dto.output.UserExistDto;
import com.example.mywebquizengine.auth.service.AuthService;
import com.example.mywebquizengine.common.SmsSender;
import com.example.mywebquizengine.common.common.Client;
import com.example.mywebquizengine.common.utils.CodeUtil;
import com.example.mywebquizengine.common.utils.JWTUtil;
import com.example.mywebquizengine.common.utils.RabbitUtil;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.service.BusinessEmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthFacadeImpl implements AuthFacade {

    @Autowired
    private AuthService authService;

    @Autowired
    private BusinessEmailSender businessEmailSender;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RabbitUtil rabbitUtil;

    @Autowired
    private SignInCodeService signInCodeService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SmsSender smsSender;

    @Override
    public AuthResult signUp(RegistrationModel registrationModel, RegistrationType type) {
        User user = authService.saveUser(registrationModel, type);
        businessEmailSender.sendWelcomeMessage(user);
        return new AuthResult(
                user.getUserId(),
                jwtUtil.generateToken(user),
                rabbitUtil.createExchange(user.getUserId())
        );
    }

    @Override
    public AuthResult signIn(AuthRequest authRequest) {
        signInCodeService.checkCodeExpire(authRequest.getUsername());
        User user = authService.signIn(authRequest);
        return new AuthResult(
                user.getUserId(),
                jwtUtil.generateToken(user),
                RabbitUtil.getExchangeName(user.getUserId())
        );
    }

    @Override
    public AuthResult signInViaExternalService(Object token) {
        User user = authService.signInViaExternalServiceToken(token);
        return new AuthResult(
                user.getUserId(),
                jwtUtil.generateToken(user),
                RabbitUtil.getExchangeName(user.getUserId())
        );
    }

    @Override
    public void createAndSendChangePasswordCodeToUser(String username, Client client) {
        User user = authService.setChangePassword(username, client);
        businessEmailSender.sendChangePasswordMessage(user, user.getChangePasswordCode(), client);
    }

    @Override
    public void updatePassword(String username, String code, String password) {
        signInCodeService.verifyChangePasswordCode(username, code);
        authService.changePassword(username, password);
    }

    @Override
    public AuthPhoneResponse signUpViaPhone(RegistrationModel registrationModel) {
        String code = CodeUtil.generateShortCode();
        registrationModel.setPassword(code);
        User user = authService.saveUser(registrationModel);
        deviceService.registerDevice(user, registrationModel.getAppleToken());
        smsSender.sendCodeToPhone(user.getPassword(), registrationModel.getUsername());
        rabbitUtil.createExchange(user.getUserId());
        return new AuthPhoneResponse(user.getPassword());
    }

    @Override
    public AuthPhoneResponse createAndSendOneTimePassword(String phone) {
        String code = authService.setOneTimePasswordCode(phone);
        smsSender.sendCodeToPhone(code, phone);
        return new AuthPhoneResponse()
                .setCode(code);
    }

    @Override
    public UserExistDto checkForExistUser(String username) {
        return new UserExistDto().setExist(
                authService.checkForExistUser(username)
        );
    }

    @Override
    public void verifyChangePasswordCode(String username, String changePasswordCode) {
        authService.verifyChangePasswordCode(username, changePasswordCode);
    }
}
