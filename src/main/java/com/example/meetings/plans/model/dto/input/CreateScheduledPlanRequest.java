package com.example.meetings.plans.model.dto.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CreateScheduledPlanRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private LocalDateTime startAt;
    private LocalDateTime expiresAt;
    private String locationName;
    private Double lat;
    private Double lon;
}

