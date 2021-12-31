package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.projection.RequestView;
import com.example.mywebquizengine.model.Request;
import com.example.mywebquizengine.service.RequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ApiRequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping(path = "/request")
    @ResponseBody
    public void sendRequest(@RequestBody Request request, @AuthenticationPrincipal Principal principal) throws JsonProcessingException, ParseException {
        requestService.sendRequest(request, principal);
        throw new ResponseStatusException(HttpStatus.OK);
    }

    @PostMapping(path = "/request/{id}/accept")
    //@PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public Long acceptRequest(@PathVariable Long id, @AuthenticationPrincipal Principal principal) {
        return requestService.acceptRequest(id, principal.getName());
    }

    @GetMapping(path = "/requests")
    public ArrayList<RequestView> getMyRequests(@AuthenticationPrincipal Principal principal) {
        return requestService.getMyRequests(principal.getName());
    }

    @PostMapping(path = "/request/{id}/reject")
    public void rejectRequest(@PathVariable Long id, @AuthenticationPrincipal Principal principal) {
        requestService.rejectRequest(id, principal.getName());
        throw new ResponseStatusException(HttpStatus.OK);
    }

    @GetMapping(path = "/sentRequests")
    public List<RequestView> getSentRequests(@AuthenticationPrincipal Principal principal) {
        return requestService.getSentRequests(principal.getName());
    }
}