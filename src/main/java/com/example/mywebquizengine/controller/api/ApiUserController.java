package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.userinfo.dto.output.ProfileView;
import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;
import com.example.mywebquizengine.model.userinfo.dto.output.AuthUserView;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ApiUserController {

    private final UserService userService;

    public ApiUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/friends")
    public List<UserCommonView> getFriends(@ApiIgnore @AuthenticationPrincipal User authUser) {
        return userService.findMyFriends(authUser.getUserId());
    }

    @DeleteMapping(path = "/friend/{userId}")
    public void deleteFriend(@PathVariable Long userId, @ApiIgnore @AuthenticationPrincipal User authUser) {
        userService.deleteFriend(userId, authUser.getUserId());
    }

    @GetMapping(path = "/findbyid")
    public UserCommonView getUserById(@RequestParam Long userId) {
        return userService.getUserView(userId);
    }

    @GetMapping(path = "/authuser")
    public AuthUserView getApiAuthUser(@ApiIgnore @AuthenticationPrincipal User authUser) {
        return userService.getAuthUser(authUser.getUserId());
    }

    @GetMapping(path = "/user/{userId}/profile")
    public ProfileView getProfile(@PathVariable Long userId) {
        return userService.getUserProfileById(userId);
    }


    @PutMapping(path = "/user", consumes = {"application/json"})
    public void changeUser(@RequestBody User user,
                           @ApiIgnore @AuthenticationPrincipal User authUser) {
        userService.updateUser(user.getLastName(), user.getFirstName(), authUser.getUserId());
    }

}
