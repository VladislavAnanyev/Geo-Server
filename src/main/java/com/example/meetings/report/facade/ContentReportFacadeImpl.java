package com.example.meetings.report.facade;

import com.example.meetings.report.model.dto.input.CreateContentReportRequest;
import com.example.meetings.report.service.ContentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentReportFacadeImpl implements ContentReportFacade {

    private final ContentReportService contentReportService;

    @Override
    @Transactional
    public void createReport(Long authUserId, CreateContentReportRequest request) {
        contentReportService.createReport(authUserId, request.getTargetType(), request.getTargetId());
    }
}
