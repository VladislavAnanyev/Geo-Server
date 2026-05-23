package com.example.meetings.subscription.facade;

import com.example.meetings.subscription.model.dto.input.GrantSubscriptionRequest;
import com.example.meetings.subscription.model.dto.output.SubscriptionViewDto;

public interface SubscriptionFacade {
    SubscriptionViewDto grantSubscription(Long adminUserId, Long targetUserId, GrantSubscriptionRequest request);

    SubscriptionViewDto getMySubscription(Long authUserId);
}
