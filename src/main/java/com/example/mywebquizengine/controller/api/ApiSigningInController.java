package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.common.SuccessfulResponse;
import com.example.mywebquizengine.model.userinfo.*;
import com.example.mywebquizengine.model.userinfo.dto.input.*;
import com.example.mywebquizengine.model.userinfo.dto.output.AuthResponse;
import com.example.mywebquizengine.model.userinfo.dto.output.AuthResult;
import com.example.mywebquizengine.service.AuthPhoneResponse;
import com.example.mywebquizengine.service.AuthService;
import com.example.mywebquizengine.model.common.Client;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api")
public class ApiSigningInController {

    private final AuthService authService;

    public ApiSigningInController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/signin")
    public AuthResponse jwtSignIn(@Valid @RequestBody AuthRequest authRequest) {
        return new AuthResponse(authService.signInViaApi(authRequest));
    }

    @PostMapping(path = "/signup")
    public AuthResponse signup(@Valid @RequestBody RegistrationRequest request) {
        AuthResult authResult = authService.signUp(RegistrationModelMapper.map(request), RegistrationType.BASIC);
        return new AuthResponse(authResult);
    }

    @PostMapping(path = "/signin/google")
    public AuthResponse googleJwt(@Valid @RequestBody GoogleToken token) {
        return new AuthResponse(authService.signInViaExternalServiceToken(token));
    }

    @PostMapping(path = "/user/send-change-password-code")
    public SuccessfulResponse sendChangePasswordCode(@RequestParam String username) {
        authService.changePassword(username, Client.MOBILE);
        return new SuccessfulResponse();
    }

    @PostMapping(path = "/user/verify-password-code")
    public SuccessfulResponse verifyChangePasswordCode(@Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        authService.verifyChangePasswordCode(
                verifyCodeRequest.getUsername(),
                verifyCodeRequest.getCode()
        );
        return new SuccessfulResponse();
    }

    @PutMapping(path = "/user/password")
    public SuccessfulResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.updatePassword(request.getUsername(), request.getCode(), request.getPassword());
        return new SuccessfulResponse();
    }

    @GetMapping(path = "/user/check-username")
    public boolean checkExistUser(@RequestParam String username) {
        return authService.checkForExistUser(username);
    }

    @PostMapping("/signup/phone")
    public SignInViaPhoneResponse signupViaPhone(@RequestBody AuthPhoneRequest authPhoneRequest) {
        AuthPhoneResponse authPhoneResponse = authService.signUpViaPhone(
                authPhoneRequest.getPhone(),
                authPhoneRequest.getFirstName(),
                authPhoneRequest.getLastName()
        );
        return new SignInViaPhoneResponse(authPhoneResponse);
    }

    @PostMapping("/signin/phone")
    public SignInViaPhoneResponse signInViaPhone(@RequestBody AuthPhoneRequest authPhoneRequest) {
        AuthPhoneResponse authPhoneResponse = authService.signInViaPhone(authPhoneRequest.getPhone());
        return new SignInViaPhoneResponse(authPhoneResponse);
    }

}
