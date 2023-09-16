package com.example.meetings.user.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.user.facade.UserFacade;
import com.example.meetings.user.model.GetAuthUserResponse;
import com.example.meetings.user.model.GetUserProfileResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = "/api/v1")
public class ApiUserController {

    private final UserFacade userFacade;

    public ApiUserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping(path = "/user/auth")
    public GetAuthUserResponse getAuthUser(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        return new GetAuthUserResponse(
                userFacade.getAuthUser(authUser.getUserId())
        );
    }

    @GetMapping(path = "/user/{userId}/profile")
    public GetUserProfileResponse getProfile(@PathVariable Long userId) {
        return new GetUserProfileResponse(
                userFacade.getUserProfileById(userId)
        );
    }

}
