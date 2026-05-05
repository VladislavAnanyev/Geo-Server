package com.example.meetings.auth.controller;

import com.example.meetings.auth.facade.AuthFacade;
import com.example.meetings.auth.model.RefreshTokenRequest;
import com.example.meetings.auth.model.dto.input.AuthEmailRequest;
import com.example.meetings.auth.model.dto.input.AuthPhoneRequest;
import com.example.meetings.auth.model.dto.input.AuthRequest;
import com.example.meetings.auth.model.dto.output.AuthResponse;
import com.example.meetings.common.model.SuccessfulResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Контроллер для входа в систему
 */
@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class ApiSigningInController {

    private final AuthFacade authFacade;

    @PostMapping(path = "/signin")
    public AuthResponse signIn(@Valid @RequestBody AuthRequest authRequest) {
        return new AuthResponse(authFacade.signIn(authRequest));
    }

    @PostMapping("/signin/phone")
    public SuccessfulResponse signInViaPhoneCodeRequest(@Valid @RequestBody AuthPhoneRequest request) {
        authFacade.signInViaPhone(request.getPhone());
        return new SuccessfulResponse();
    }

    @PostMapping("/signin/email")
    public SuccessfulResponse signInViaEmailCodeRequest(@Valid @RequestBody AuthEmailRequest request) {
        authFacade.signInViaEmail(request.getEmail());
        return new SuccessfulResponse();
    }

    @PostMapping("/refresh")
    public AuthResponse getNewAccessToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return new AuthResponse(authFacade.getNewAccessToken(refreshTokenRequest.getRefreshToken()));
    }
}
