package com.example.meetings.report.model.dto.input;

import com.example.meetings.report.model.domain.ContentReportTargetType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateContentReportRequest {

    @NotNull
    private ContentReportTargetType targetType;

    @NotNull
    private Long targetId;
}
