package com.example.meetings.user.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.user.model.dto.UserCommonView;
import com.example.meetings.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static com.example.meetings.util.Constants.ADMIN_ID;

@RestController
@RequestMapping(path = "/api/v1")
public class ApiFriendController {

    private final UserService userService;

    public ApiFriendController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/friends")
    public List<UserCommonView> getFriends(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        List<UserCommonView> friends = userService.findMyFriends(authUser.getUserId());
        friends.removeIf(userCommonView -> userCommonView.getUserId().equals(ADMIN_ID));

        return friends;
    }

    @DeleteMapping(path = "/friend/{userId}")
    public SuccessfulResponse deleteFriend(@PathVariable Long userId, @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        userService.deleteFriend(userId, authUser.getUserId());
        return new SuccessfulResponse();
    }
}
