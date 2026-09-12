package com.example.meetings.plans.model.dto.output;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlanLocationDto {
    private Double lat;
    private Double lon;
    private String locationName;
}

