package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.model.geo.Meeting;
import com.example.mywebquizengine.model.projection.RequestView;
import com.example.mywebquizengine.model.request.Request;
import com.example.mywebquizengine.model.request.RequestDto;
import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.service.RequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
public class ApiRequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping(path = "/request")
    @ResponseBody
    @Transactional
    public void sendRequest(
            @RequestBody @Valid RequestDto requestDto,
            @ApiIgnore @AuthenticationPrincipal User authUser) throws JsonProcessingException {

        Request request = new Request();

        User user = new User();
        user.setUsername(requestDto.getToUsername());

        Meeting meeting = new Meeting();
        meeting.setId(requestDto.getMeetingId());

        Message message = new Message();
        message.setContent(requestDto.getMessageContent());

        request.setTo(user);
        request.setMeeting(meeting);

        if (message.getContent() != null) {
            request.setMessage(message);
        }
        requestService.sendRequest(request, authUser.getUsername());

    }

    @PostMapping(path = "/request/{id}/accept")
    //@PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public Long acceptRequest(
            @PathVariable Long id,
            @ApiIgnore @AuthenticationPrincipal User user) throws JsonProcessingException {
        return requestService.acceptRequest(id, user.getUsername());
    }

    @GetMapping(path = "/requests")
    public ArrayList<RequestView> getMyRequests(@ApiIgnore @AuthenticationPrincipal User user) {
        return requestService.getMyRequests(user.getUsername());
    }

    @PostMapping(path = "/request/{id}/reject")
    public void rejectRequest(@PathVariable Long id, @ApiIgnore @AuthenticationPrincipal User user) {
        requestService.rejectRequest(id, user.getUsername());
    }

    @GetMapping(path = "/sentRequests")
    public List<RequestView> getSentRequests(@ApiIgnore @AuthenticationPrincipal User user) {
        return requestService.getSentRequests(user.getUsername());
    }
}
