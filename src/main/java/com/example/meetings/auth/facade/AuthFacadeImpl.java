package com.example.meetings.auth.facade;

import com.example.meetings.auth.model.UserToken;
import com.example.meetings.auth.model.dto.input.AuthRequest;
import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.auth.model.dto.output.AuthResult;
import com.example.meetings.auth.service.*;
import com.example.meetings.chat.model.SendMessageModel;
import com.example.meetings.chat.service.MessageService;
import com.example.meetings.common.service.CodeSenderService;
import com.example.meetings.common.utils.*;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.service.FriendService;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.example.meetings.util.Constants.ADMIN_ID;
import static com.example.meetings.util.Constants.CONTENT;

@Service
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {
    private final AuthService authService;
    private final JWTUtil jwtUtil;
    private final RabbitUtil rabbitUtil;
    private final DeviceService deviceService;
    private final CodeGenerationService codeGenerationService;
    private final CodeSenderService codeSenderService;
    private final TokenService tokenService;
    private final UserService userService;
    private final MessageService messageService;
    private final FriendService friendService;

    @Override
    public AuthResult signIn(AuthRequest authRequest) {
        User user = authService.authenticate(authRequest);
        deviceService.processDevice(user, authRequest.getFcmToken());

        return new AuthResult(
                user.getUserId(),
                jwtUtil.generateToken(user),
                tokenService.createToken(user.getUserId()),
                RabbitUtil.getExchangeName(user.getUserId())
        );
    }

    @Override
    public void signUpViaPhone(RegistrationModel data) {
        String code = codeGenerationService.generate();
        data.setPassword(code);

        User user = userService.saveUser(data);
        codeSenderService.sendCodeToPhone(code, data.getPhoneNumber());
        rabbitUtil.createExchange(user.getUserId());
        Long dialogId = messageService.createDialog(ADMIN_ID, user.getUserId());
        messageService.saveMessage(
                new SendMessageModel()
                        .setSenderId(ADMIN_ID)
                        .setContent(CONTENT)
                        .setDialogId(dialogId)
                        .setUniqueCode(UUID.randomUUID().toString())
        );
        friendService.makeFriends(ADMIN_ID, user.getUserId());
    }

    @Override
    public void signInViaPhone(String phone) {
        if (!userService.isUserExist(phone)) {
            signUpViaPhone(new RegistrationModel().setPhoneNumber(phone));
            return;
        }

        String code = codeGenerationService.generate();
        authService.setOneTimePasswordCode(phone, code);
        codeSenderService.sendCodeToPhone(code, phone);
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
