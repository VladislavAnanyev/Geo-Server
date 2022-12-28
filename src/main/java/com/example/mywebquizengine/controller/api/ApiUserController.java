package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.common.model.SuccessfulResponse;
import com.example.mywebquizengine.user.facade.UserFacade;
import com.example.mywebquizengine.user.model.GetAuthUserResponse;
import com.example.mywebquizengine.user.model.GetUserProfileResponse;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.service.UserService;
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
