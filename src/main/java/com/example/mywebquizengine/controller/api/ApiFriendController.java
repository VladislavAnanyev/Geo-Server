package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.user.model.dto.UserCommonView;
import com.example.mywebquizengine.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class ApiFriendController {

    private final UserService userService;

    public ApiFriendController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/friends")
    public List<UserCommonView> getFriends(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        return userService.findMyFriends(authUser.getUserId());
    }

    @DeleteMapping(path = "/friend/{userId}")
    public void deleteFriend(@PathVariable Long userId, @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        userService.deleteFriend(userId, authUser.getUserId());
    }
}
