package com.example.meetings.request.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.request.facade.RequestFacade;
import com.example.meetings.request.model.dto.input.RequestDto;
import com.example.meetings.request.model.dto.output.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1")
public class ApiRequestController {

    @Autowired
    private RequestFacade requestFacade;

    @ApiOperation(value = "Отправить заявку на добавление в друзья")
    @PostMapping(path = "/request")
    public SuccessfulResponse sendRequest(@RequestBody @Valid RequestDto requestDto, @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        requestFacade.sendRequest(
                requestDto.getMeetingId(),
                authUser.getUserId(),
                requestDto.getToUserId(),
                requestDto.getMessageContent()
        );
        return new SuccessfulResponse();
    }

    @ApiOperation(value = "Принять заявку на добавление в друзья")
    @PostMapping(path = "/request/{id}/accept")
    public AcceptRequestResponse acceptRequest(@PathVariable Long id, @ApiIgnore @AuthenticationPrincipal AuthUserDetails user) {
        return new AcceptRequestResponse(
                requestFacade.acceptRequest(id, user.getUserId())
        );
    }

    @ApiOperation(value = "Получить список заявок на добавление в друзья к аутентифицированному пользователю")
    @GetMapping(path = "/requests")
    public GetRequestsToUserResponse getRequestsFromUser(@ApiIgnore @AuthenticationPrincipal AuthUserDetails user) {
        return new GetRequestsToUserResponse(
                requestFacade.getRequestsToUser(user.getUserId())
        );
    }

    @ApiOperation(value = "Отклонить заявку на добавление в друзья")
    @PostMapping(path = "/request/{id}/reject")
    public SuccessfulResponse rejectRequest(@PathVariable Long id, @ApiIgnore @AuthenticationPrincipal AuthUserDetails user) {
        requestFacade.rejectRequest(id, user.getUserId());
        return new SuccessfulResponse();
    }

    @ApiOperation(value = "Получить список отправленных пользователем заявок на добавление в друзья")
    @GetMapping(path = "/requests/sent")
    public GetSentRequestsResponse getSentRequests(@ApiIgnore @AuthenticationPrincipal AuthUserDetails user) {
        return new GetSentRequestsResponse(
                requestFacade.getSentToUserRequests(user.getUserId())
        );
    }

    @PostMapping(path = "/requests/meeting/{meetingId}")
    @ApiOperation(value = "Не показывать указанную встречу в рекомендациях сегодня")
    public SuccessfulResponse doNotShowInRecommendation(@ApiIgnore @AuthenticationPrincipal AuthUserDetails user, @PathVariable Long meetingId) {
        requestFacade.doNotShowInRecommendation(meetingId, user.getUserId());
        return new SuccessfulResponse();
    }
}
