package com.example.meetings.report.repository;

import com.example.meetings.report.model.domain.ContentReport;
import com.example.meetings.report.model.domain.ContentReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentReportRepository extends JpaRepository<ContentReport, Long> {

    boolean existsByReporter_UserIdAndTargetTypeAndTargetId(
            Long reporterUserId,
            ContentReportTargetType targetType,
            Long targetId
    );
}
