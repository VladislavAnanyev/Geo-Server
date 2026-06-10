package com.example.meetings.geolocation.sharing.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.geolocation.sharing.facade.LocationSharingFacade;
import com.example.meetings.geolocation.sharing.model.dto.input.LocationSharingFriendRequest;
import com.example.meetings.geolocation.sharing.model.dto.output.GetLocationSharingListResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/geolocation-sharing")
@RequiredArgsConstructor
public class ApiLocationSharingController {

    private final LocationSharingFacade locationSharingFacade;

    @ApiOperation("Отправить приглашение на обмен геолокацией другу")
    @PostMapping("/request")
    public SuccessfulResponse sendInvite(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                         @RequestBody @Valid LocationSharingFriendRequest request) {
        locationSharingFacade.sendInvite(authUser.getUserId(), request);
        return new SuccessfulResponse();
    }

    @ApiOperation("Входящие приглашения на обмен геолокацией")
    @GetMapping("/requests/incoming")
    public GetLocationSharingListResponse getIncomingPending(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                                             @RequestParam(value = "userId", required = false) Long userId) {
        return locationSharingFacade.getIncomingPending(authUser.getUserId(), userId);
    }

    @ApiOperation("Исходящие приглашения на обмен геолокацией")
    @GetMapping("/requests/outgoing")
    public GetLocationSharingListResponse getOutgoingPending(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                                             @RequestParam(value = "userId", required = false) Long userId) {
        return locationSharingFacade.getOutgoingPending(authUser.getUserId(), userId);
    }

    @ApiOperation("Активный обмен геолокацией с друзьями")
    @GetMapping("/active")
    public GetLocationSharingListResponse getActive(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        return locationSharingFacade.getActive(authUser.getUserId());
    }

    @ApiOperation("Принять приглашение на обмен геолокацией")
    @PostMapping("/request/{id}/accept")
    public SuccessfulResponse accept(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                     @PathVariable Long id) {
        locationSharingFacade.accept(authUser.getUserId(), id);
        return new SuccessfulResponse();
    }

    @ApiOperation("Отклонить приглашение на обмен геолокацией")
    @PostMapping("/request/{id}/reject")
    public SuccessfulResponse reject(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                     @PathVariable Long id) {
        locationSharingFacade.reject(authUser.getUserId(), id);
        return new SuccessfulResponse();
    }

    @ApiOperation("Прекратить обмен геолокацией с другом")
    @PostMapping("/revoke")
    public SuccessfulResponse revoke(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                     @RequestBody @Valid LocationSharingFriendRequest request) {
        locationSharingFacade.revoke(authUser.getUserId(), request);
        return new SuccessfulResponse();
    }
}
