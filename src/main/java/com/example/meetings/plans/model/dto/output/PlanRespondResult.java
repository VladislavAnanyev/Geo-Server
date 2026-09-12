package com.example.meetings.plans.model.dto.output;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlanRespondResult {
    private Long planId;
    private Long responseId;
    private String status;
}

