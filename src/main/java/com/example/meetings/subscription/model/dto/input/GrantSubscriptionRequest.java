package com.example.meetings.subscription.model.dto.input;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GrantSubscriptionRequest {
    @NotBlank
    private String planCode;
    @NotNull
    @Min(1)
    private Integer durationDays;
    private String externalPaymentRef;
    private String note;
}
