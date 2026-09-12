package com.example.meetings.plans.model.dto.output;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PlanResponseListResult {
    private List<PlanResponseViewDto> items;
    private int page;
    private int size;
    private long total;
}
