package com.example.meetings.meeting.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.meeting.facade.MeetingsFacade;
import com.example.meetings.meeting.model.GetMeetingsResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1")
public class ApiMeetingsController {

    private final MeetingsFacade meetingsFacade;

    @ApiOperation(value = "Получить список встреч за указанную дату")
    @GetMapping(path = "/meetings")
    public GetMeetingsResponse getMeetings(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DATE) LocalDate startAt,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DATE) LocalDate endAt) {
        return new GetMeetingsResponse(meetingsFacade.getMeetings(authUser.getUserId(), startAt, endAt));
    }

}
