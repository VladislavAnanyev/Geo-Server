package com.example.meetings.plans.model.dto.output;

import com.example.meetings.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponseListResponse extends SuccessfulResponse {
    private PlanResponseListResult result;
}
