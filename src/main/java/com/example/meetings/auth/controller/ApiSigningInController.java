package com.example.meetings.auth.controller;

import com.example.meetings.auth.facade.AuthFacade;
import com.example.meetings.auth.model.RefreshTokenRequest;
import com.example.meetings.auth.model.RegistrationType;
import com.example.meetings.auth.model.dto.input.*;
import com.example.meetings.auth.model.dto.output.AuthPhoneResult;
import com.example.meetings.auth.model.dto.output.AuthResponse;
import com.example.meetings.auth.model.dto.output.AuthResult;
import com.example.meetings.auth.model.dto.output.SignInViaPhoneResponse;
import com.example.meetings.common.model.Client;
import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.user.model.CheckUserExistsResponse;
import com.example.meetings.user.model.GetAuthCodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.meetings.auth.util.RegistrationModelMapper.map;

/**
 * Контроллер для входа в систему
 */
@RestController
@RequestMapping(path = "/api/v1")
public class ApiSigningInController {

    @Autowired
    private AuthFacade authFacade;

    @PostMapping(path = "/signin")
    public AuthResponse signIn(@Valid @RequestBody AuthRequest authRequest) {
        return new AuthResponse(
                authFacade.signIn(authRequest)
        );
    }

    @PostMapping(path = "/signup")
    public AuthResponse signup(@Valid @RequestBody RegistrationRequest request) {
        AuthResult authResult = authFacade.signUp(
                map(request),
                RegistrationType.BASIC
        );
        return new AuthResponse(authResult);
    }

    @PostMapping(path = "/signin/google")
    public AuthResponse googleJwt(@Valid @RequestBody GoogleToken token) {
        return new AuthResponse(
                authFacade.signInViaExternalService(token)
        );
    }

    @PostMapping(path = "/user/send-change-password-code")
    public SuccessfulResponse sendChangePasswordCode(@RequestParam String username) {
        authFacade.createAndSendChangePasswordCodeToUser(username, Client.MOBILE);
        return new SuccessfulResponse();
    }

    @PostMapping(path = "/user/verify-password-code")
    public SuccessfulResponse verifyChangePasswordCode(@Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        authFacade.verifyChangePasswordCode(
                verifyCodeRequest.getUsername(),
                verifyCodeRequest.getCode()
        );
        return new SuccessfulResponse();
    }

    @PutMapping(path = "/user/password")
    public SuccessfulResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authFacade.updatePassword(request.getUsername(), request.getCode(), request.getPassword());
        return new SuccessfulResponse();
    }

    @GetMapping(path = "/user/check-username")
    public CheckUserExistsResponse checkExistUser(@RequestParam String username) {
        return new CheckUserExistsResponse(
                authFacade.checkForExistUser(username)
        );
    }

    @PostMapping("/signup/phone")
    public SignInViaPhoneResponse signupViaPhone(@RequestBody AuthPhoneRequest authPhoneRequest) {
        return new SignInViaPhoneResponse(
                authFacade.signUpViaPhone(
                        new RegistrationModel()
                                .setUsername(authPhoneRequest.getPhone())
                                .setLastName(authPhoneRequest.getLastName())
                                .setFirstName(authPhoneRequest.getFirstName())
                                .setEmail(authPhoneRequest.getPhone())
                )
        );
    }

    @PostMapping("/signin/phone")
    public GetAuthCodeResponse signInViaPhoneCodeRequest(@RequestBody GetCodeRequest getCodeRequest) {
        AuthPhoneResult authPhoneResult = authFacade.createAndSendOneTimePassword(getCodeRequest.getPhone());
        return new GetAuthCodeResponse(
                authPhoneResult
        );
    }

    @PostMapping("/refresh")
    public AuthResponse getNewAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return new AuthResponse(
                authFacade.getNewAccessToken(refreshTokenRequest.getRefreshToken())
        );
    }
}
