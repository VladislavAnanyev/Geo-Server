package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.auth.model.RegistrationType;
import com.example.mywebquizengine.auth.model.dto.output.*;
import com.example.mywebquizengine.auth.service.AuthService;
import com.example.mywebquizengine.auth.util.RegistrationModelMapper;
import com.example.mywebquizengine.common.common.SuccessfulResponse;
import com.example.mywebquizengine.auth.model.dto.input.*;
import com.example.mywebquizengine.common.common.Client;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Контроллер для входа в систему
 */
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
        return new AuthResponse(
                authService.signInViaExternalServiceToken(token)
        );
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
    public SuccessfulResponse checkExistUser(@RequestParam String username) {
        SuccessfulResponse successfulResponse = new SuccessfulResponse();
        successfulResponse.setResult(authService.checkForExistUser(username));
        return successfulResponse;
    }

    @PostMapping("/signup/phone")
    public SignInViaPhoneResponse signupViaPhone(@RequestBody AuthPhoneRequest authPhoneRequest) {

        RegistrationModel registrationModel = new RegistrationModel();
        registrationModel.setUsername(authPhoneRequest.getPhone());
        registrationModel.setLastName(authPhoneRequest.getLastName());
        registrationModel.setFirstName(authPhoneRequest.getFirstName());
        registrationModel.setEmail(authPhoneRequest.getPhone());

        AuthPhoneResponse authPhoneResponse = authService.signUpViaPhone(
                registrationModel
        );
        return new SignInViaPhoneResponse(authPhoneResponse);
    }

    @PostMapping("/signin/phone")
    public SuccessfulResponse signInViaPhone(@RequestBody GetCodeRequest getCodeRequest) {
        AuthPhoneResponse authPhoneResponse = authService.generateCodeForSignInViaPhone(getCodeRequest.getPhone());
        SuccessfulResponse successfulResponse = new SuccessfulResponse();
        successfulResponse.setResult(authPhoneResponse);
        return successfulResponse;
    }
}
