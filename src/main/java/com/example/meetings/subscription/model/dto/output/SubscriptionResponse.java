package com.example.meetings.subscription.model.dto.output;

import com.example.meetings.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse extends SuccessfulResponse {
    private SubscriptionViewDto result;
}
