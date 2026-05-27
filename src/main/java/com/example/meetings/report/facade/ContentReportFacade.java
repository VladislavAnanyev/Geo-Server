package com.example.meetings.report.facade;

import com.example.meetings.report.model.dto.input.CreateContentReportRequest;

public interface ContentReportFacade {

    void createReport(Long authUserId, CreateContentReportRequest request);
}
