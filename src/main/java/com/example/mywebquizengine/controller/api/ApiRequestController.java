package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.chat.domain.Message;
import com.example.mywebquizengine.model.geo.domain.Meeting;
import com.example.mywebquizengine.model.request.dto.output.RequestView;
import com.example.mywebquizengine.model.request.domain.Request;
import com.example.mywebquizengine.model.request.dto.input.RequestDto;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.RequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ApiRequestController {

    private final RequestService requestService;

    public ApiRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping(path = "/request")
    @ResponseBody
    @Transactional
    public void sendRequest(
            @RequestBody @Valid RequestDto requestDto,
            @ApiIgnore @AuthenticationPrincipal User authUser) throws JsonProcessingException {

        Request request = new Request();

        User user = new User();
        user.setUserId(requestDto.getToUserId());

        Meeting meeting = new Meeting();
        meeting.setMeetingId(requestDto.getMeetingId());

        Message message = new Message();
        message.setContent(requestDto.getMessageContent());

        request.setTo(user);
        request.setMeeting(meeting);

        if (message.getContent() != null) {
            request.setMessage(message);
        }
        requestService.sendRequest(request, authUser.getUserId());

    }

    @PostMapping(path = "/request/{id}/accept")
    //@PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public Long acceptRequest(
            @PathVariable Long id,
            @ApiIgnore @AuthenticationPrincipal User user) throws JsonProcessingException {
        return requestService.acceptRequest(id, user.getUserId());
    }

    @GetMapping(path = "/requests")
    public ArrayList<RequestView> getMyRequests(@ApiIgnore @AuthenticationPrincipal User user) {
        return requestService.getMyRequests(user.getUserId());
    }

    @PostMapping(path = "/request/{id}/reject")
    public void rejectRequest(@PathVariable Long id, @ApiIgnore @AuthenticationPrincipal User user) {
        requestService.rejectRequest(id, user.getUserId());
    }

    @GetMapping(path = "/sentRequests")
    public List<RequestView> getSentRequests(@ApiIgnore @AuthenticationPrincipal User user) {
        return requestService.getSentRequests(user.getUserId());
    }
}
