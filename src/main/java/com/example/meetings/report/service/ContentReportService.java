package com.example.meetings.report.service;

import com.example.meetings.report.model.domain.ContentReport;
import com.example.meetings.report.model.domain.ContentReportTargetType;
import com.example.meetings.report.repository.ContentReportRepository;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.meetings.report.model.domain.ContentReportStatus.PENDING;

/**
 * Сервис создания жалоб на контент.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentReportService {

    private final ContentReportRepository contentReportRepository;
    private final UserService userService;

    /**
     * Создаёт жалобу на контент, если аналогичная жалоба от этого пользователя ещё не существует.
     *
     * @param reporterUserId идентификатор пользователя, отправившего жалобу
     * @param targetType     тип объекта жалобы
     * @param targetId       идентификатор объекта жалобы
     */
    @Transactional
    public void createReport(Long reporterUserId, ContentReportTargetType targetType, Long targetId) {
        if (contentReportRepository.existsByReporter_UserIdAndTargetTypeAndTargetId(
                reporterUserId, targetType, targetId)) {
            log.debug("Duplicate content report: reporter={}, type={}, target={}",
                    reporterUserId, targetType, targetId);
            return;
        }

        contentReportRepository.save(new ContentReport()
                .setReporter(userService.loadUserByUserIdProxy(reporterUserId))
                .setTargetType(targetType)
                .setTargetId(targetId)
                .setStatus(PENDING)
                .setCreatedAt(LocalDateTime.now()));
    }
}
