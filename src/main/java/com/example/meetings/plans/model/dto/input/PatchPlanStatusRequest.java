package com.example.meetings.plans.model.dto.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PatchPlanStatusRequest {
    @NotBlank
    private String status;
}

