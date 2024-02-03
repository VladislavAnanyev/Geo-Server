package com.example.meetings.auth.facade;

import com.example.meetings.auth.model.UserToken;
import com.example.meetings.auth.model.dto.input.AuthRequest;
import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.auth.model.dto.output.AuthPhoneResult;
import com.example.meetings.auth.model.dto.output.AuthResult;
import com.example.meetings.auth.service.*;
import com.example.meetings.common.service.SmsSender;
import com.example.meetings.common.utils.*;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.meetings.auth.model.RegistrationType.PHONE;

@Service
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final AuthService authService;
    private final JWTUtil jwtUtil;
    private final RabbitUtil rabbitUtil;
    private final SignInCodeService signInCodeService;
    private final DeviceService deviceService;
    private final SmsSender smsSender;
    private final TokenService tokenService;
    private final UserService userService;

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
    public AuthPhoneResult signUpViaPhone(RegistrationModel registrationData) {
        String code = CodeUtil.generateShortCode();
        registrationData.setPassword(code);
        User user = authService.saveUser(registrationData, PHONE);
        deviceService.registerDevice(user, registrationData.getAppleToken());
        smsSender.sendCodeToPhone(code, registrationData.getUsername());
        rabbitUtil.createExchange(user.getUserId());

        return new AuthPhoneResult()
                .setCode(code);
    }

    @Override
    public AuthPhoneResult signInViaPhone(String phone) {
        if (!authService.isUserExist(phone)) {
            return signUpViaPhone(new RegistrationModel().setUsername(phone));
        }

        String code = authService.setOneTimePasswordCode(phone);
        smsSender.sendCodeToPhone(code, phone);

        return new AuthPhoneResult()
                .setCode(code);
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
