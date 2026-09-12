package com.example.meetings.plans.model.dto.output;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PlanListResult {
    private List<PlanViewDto> items;
    private int page;
    private int size;
    private long total;
}

