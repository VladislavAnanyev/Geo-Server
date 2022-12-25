package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.user.model.dto.ProfileView;
import com.example.mywebquizengine.user.model.dto.UserCommonView;
import com.example.mywebquizengine.user.model.dto.AuthUserView;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class ApiUserController {

    private final UserService userService;

    public ApiUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/findbyid")
    public UserCommonView getUserById(@RequestParam Long userId) {
        return userService.getUserView(userId);
    }

    @GetMapping(path = "/authuser")
    public AuthUserView getApiAuthUser(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        return userService.getAuthUser(authUser.getUserId());
    }

    @GetMapping(path = "/user/{userId}/profile")
    public ProfileView getProfile(@PathVariable Long userId) {
        return userService.getUserProfileById(userId);
    }


    @PutMapping(path = "/user", consumes = {"application/json"})
    public void changeUser(@RequestBody User user,
                           @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        userService.updateUser(user.getLastName(), user.getFirstName(), authUser.getUserId());
    }

}
