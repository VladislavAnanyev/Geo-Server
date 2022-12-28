package com.example.mywebquizengine.auth.facade;

import com.example.mywebquizengine.auth.model.UserToken;
import com.example.mywebquizengine.auth.model.RegistrationType;
import com.example.mywebquizengine.auth.model.dto.input.AuthRequest;
import com.example.mywebquizengine.auth.model.dto.input.RegistrationModel;
import com.example.mywebquizengine.auth.model.dto.output.AuthPhoneResult;
import com.example.mywebquizengine.auth.model.dto.output.AuthResult;
import com.example.mywebquizengine.auth.model.dto.output.UserExistDto;
import com.example.mywebquizengine.auth.service.AuthService;
import com.example.mywebquizengine.auth.service.DeviceService;
import com.example.mywebquizengine.auth.service.SignInCodeService;
import com.example.mywebquizengine.auth.service.TokenService;
import com.example.mywebquizengine.common.service.SmsSender;
import com.example.mywebquizengine.common.model.Client;
import com.example.mywebquizengine.common.utils.CodeUtil;
import com.example.mywebquizengine.common.utils.JWTUtil;
import com.example.mywebquizengine.common.utils.RabbitUtil;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.service.BusinessEmailSender;
import com.example.mywebquizengine.user.service.UserService;
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
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @Override
    public AuthResult signUp(RegistrationModel registrationModel, RegistrationType type) {
        User user = authService.saveUser(registrationModel, type);
        businessEmailSender.sendWelcomeMessage(user);
        return new AuthResult(
                user.getUserId(),
                jwtUtil.generateToken(user),
                tokenService.createToken(user.getUserId()),
                rabbitUtil.createExchange(user.getUserId())
        );
    }

    @Override
    public AuthResult signIn(AuthRequest authRequest) {
        signInCodeService.checkCodeExpire(authRequest.getUsername());
        User user = authService.authenticate(authRequest);
        return new AuthResult(
                user.getUserId(),
                jwtUtil.generateToken(user),
                tokenService.createToken(user.getUserId()),
                RabbitUtil.getExchangeName(user.getUserId())
        );
    }

    @Override
    public AuthResult signInViaExternalService(Object token) {
        User user = authService.signInViaExternalServiceToken(token);
        return new AuthResult(
                user.getUserId(),
                jwtUtil.generateToken(user),
                tokenService.createToken(user.getUserId()),
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
    public AuthPhoneResult signUpViaPhone(RegistrationModel registrationModel) {
        String code = CodeUtil.generateShortCode();
        registrationModel.setPassword(code);
        User user = authService.saveUser(registrationModel);
        deviceService.registerDevice(user, registrationModel.getAppleToken());
        smsSender.sendCodeToPhone(user.getPassword(), registrationModel.getUsername());
        rabbitUtil.createExchange(user.getUserId());
        return new AuthPhoneResult()
                .setCode(user.getPassword());
    }

    @Override
    public AuthPhoneResult createAndSendOneTimePassword(String phone) {
        String code = authService.setOneTimePasswordCode(phone);
        smsSender.sendCodeToPhone(code, phone);
        return new AuthPhoneResult()
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

    @Override
    public AuthResult getNewAccessToken(String refreshToken) {
        UserToken userToken = authService.updateRefreshToken(refreshToken);
        return new AuthResult()
                .setRefreshToken(userToken.getRefreshToken())
                .setJwtToken(jwtUtil.generateToken(userService.loadUserByUserId(userToken.getUserId())))
                .setUserId(userToken.getUserId());
    }
}
