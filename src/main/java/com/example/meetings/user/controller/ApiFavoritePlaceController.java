package com.example.meetings.user.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.user.facade.UserFacade;
import com.example.meetings.user.favoriteplace.model.AddFavoritePlaceRequest;
import com.example.meetings.user.model.AddFavoritePlaceResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/user/favorite-places")
public class ApiFavoritePlaceController {

    private final UserFacade userFacade;

    @PostMapping
    @ApiOperation(value = "Добавить любимое место в профиль")
    public AddFavoritePlaceResponse addFavoritePlace(
            @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
            @Valid @RequestBody AddFavoritePlaceRequest request
    ) {
        return new AddFavoritePlaceResponse(userFacade.addFavoritePlace(authUser.getUserId(), request));
    }

    @DeleteMapping(path = "/{id}")
    @ApiOperation(value = "Удалить любимое место из профиля")
    public SuccessfulResponse removeFavoritePlace(
            @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
            @PathVariable Long id
    ) {
        userFacade.removeFavoritePlace(authUser.getUserId(), id);
        return new SuccessfulResponse();
    }
}
