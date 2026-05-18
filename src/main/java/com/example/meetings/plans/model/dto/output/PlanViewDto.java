package com.example.meetings.plans.model.dto.output;

import com.example.meetings.plans.model.domain.PlanMode;
import com.example.meetings.plans.model.domain.PlanResponseStatus;
import com.example.meetings.plans.model.domain.PlanType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class PlanViewDto {
    private Long planId;
    private PlanType type;
    private PlanMode mode;
    private PlanUserDto user;
    private String title;
    private String description;
    private String whenLabel;
    private LocalDateTime startAt;
    private String etaHint;
    private PlanLocationDto location;
    private boolean isOwnPlan;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long dialogId;
    private PlanResponseStatus responseStatus;
    private Long pendingResponsesCount;
}

