package com.example.meetings.plans.model.dto.output;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlanUserDto {
    private Long userId;
    private String firstName;
    private Integer age;
    private String avatarUrl;
}

