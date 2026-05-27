package com.example.meetings.report.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.report.facade.ContentReportFacade;
import com.example.meetings.report.model.dto.input.CreateContentReportRequest;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiContentReportController {

    private final ContentReportFacade contentReportFacade;

    @ApiOperation("Пожаловаться на контент")
    @PostMapping("/reports")
    public SuccessfulResponse createReport(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                           @RequestBody @Valid CreateContentReportRequest request) {
        contentReportFacade.createReport(authUser.getUserId(), request);
        return new SuccessfulResponse();
    }
}
