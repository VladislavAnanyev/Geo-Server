package com.example.meetings.plans.model.dto.output;

import com.example.meetings.plans.model.domain.PlanResponseStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class PlanResponseViewDto {
    private Long responseId;
    private Long userId;
    private String firstName;
    private String avatarUrl;
    private PlanResponseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;
}
