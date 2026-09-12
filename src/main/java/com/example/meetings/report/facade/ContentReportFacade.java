package com.example.meetings.report.facade;

import com.example.meetings.report.model.dto.input.CreateContentReportRequest;

/**
 * Фасад сценариев API жалоб на контент.
 */
public interface ContentReportFacade {

    /**
     * Создаёт жалобу на указанный объект контента от имени пользователя.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    тип объекта и его идентификатор
     */
    void createReport(Long authUserId, CreateContentReportRequest request);
}
