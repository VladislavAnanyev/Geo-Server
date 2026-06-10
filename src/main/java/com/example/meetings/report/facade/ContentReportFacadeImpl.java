package com.example.meetings.report.facade;

import com.example.meetings.report.model.dto.input.CreateContentReportRequest;
import com.example.meetings.report.service.ContentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация фасада жалоб на контент.
 */
@Service
@RequiredArgsConstructor
public class ContentReportFacadeImpl implements ContentReportFacade {

    private final ContentReportService contentReportService;

    /**
     * Создаёт жалобу на указанный объект контента от имени пользователя.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    тип объекта и его идентификатор
     */
    @Override
    @Transactional
    public void createReport(Long authUserId, CreateContentReportRequest request) {
        contentReportService.createReport(authUserId, request.getTargetType(), request.getTargetId());
    }
}
