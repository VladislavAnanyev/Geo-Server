package com.example.meetings.subscription.model.dto.output;

import com.example.meetings.subscription.model.domain.SubscriptionStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class SubscriptionViewDto {
    private Long subscriptionId;
    private Long userId;
    private boolean active;
    private SubscriptionStatus status;
    private String planCode;
    private LocalDateTime startsAt;
    private LocalDateTime expiresAt;
}
