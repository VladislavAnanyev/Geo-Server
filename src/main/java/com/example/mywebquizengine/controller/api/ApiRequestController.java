package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.request.RequestFacade;
import com.example.mywebquizengine.request.model.domain.RequestStatus;
import com.example.mywebquizengine.request.model.dto.output.RequestView;
import com.example.mywebquizengine.request.model.dto.input.RequestDto;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.request.service.RequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class ApiRequestController {

    @Autowired
    private RequestFacade requestFacade;

    @PostMapping(path = "/request")
    @ResponseBody
    @Transactional
    public void sendRequest(@RequestBody @Valid RequestDto requestDto, @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        requestFacade.sendRequest(
                requestDto.getMeetingId(),
                authUser.getUserId(),
                requestDto.getToUserId(),
                requestDto.getMessageContent()
        );
    }

    @PostMapping(path = "/request/{id}/accept")
    public Long acceptRequest(
            @PathVariable Long id,
            @ApiIgnore @AuthenticationPrincipal AuthUserDetails user) {
        return requestFacade.acceptRequest(id, user.getUserId());
    }

    @GetMapping(path = "/requests")
    public ArrayList<RequestView> getMyRequests(@ApiIgnore @AuthenticationPrincipal AuthUserDetails user) {
        return requestFacade.getMyRequests(user.getUserId());
    }

    @PostMapping(path = "/request/{id}/reject")
    public void rejectRequest(@PathVariable Long id, @ApiIgnore @AuthenticationPrincipal AuthUserDetails user) {
        requestFacade.rejectRequest(id, user.getUserId());
    }

    @GetMapping(path = "/sentRequests")
    public List<RequestView> getSentRequests(@ApiIgnore @AuthenticationPrincipal AuthUserDetails user) {
        return requestFacade.getSentRequests(user.getUserId());
    }
}
