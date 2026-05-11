package com.example.meetings.plans.model.dto.input;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ToggleLivePlanRequest {
    @NotNull
    private Boolean enabled;
    private Double lat;
    private Double lon;
}

