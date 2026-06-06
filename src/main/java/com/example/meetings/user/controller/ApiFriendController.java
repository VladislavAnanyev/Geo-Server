package com.example.meetings.user.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.user.facade.FriendFacade;
import com.example.meetings.user.model.dto.UserCommonView;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * REST API для списка друзей и удаления из друзей.
 */
@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class ApiFriendController {

    private final FriendFacade friendFacade;

    /**
     * Возвращает список друзей текущего пользователя.
     */
    @ApiOperation("Получить список друзей")
    @GetMapping(path = "/friends")
    public List<UserCommonView> getFriends(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        return friendFacade.getFriends(authUser.getUserId());
    }

    /**
     * Удаляет пользователя из друзей.
     */
    @ApiOperation("Удалить из друзей")
    @DeleteMapping(path = "/friend/{userId}")
    public SuccessfulResponse deleteFriend(@PathVariable Long userId,
                                           @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        friendFacade.deleteFriend(userId, authUser.getUserId());
        return new SuccessfulResponse();
    }
}
