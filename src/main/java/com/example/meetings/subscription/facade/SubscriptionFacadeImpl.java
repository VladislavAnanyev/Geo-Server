package com.example.meetings.subscription.facade;

import com.example.meetings.subscription.model.dto.input.GrantSubscriptionRequest;
import com.example.meetings.subscription.model.dto.output.SubscriptionViewDto;
import com.example.meetings.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.meetings.util.Constants.ADMIN_ID;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class SubscriptionFacadeImpl implements SubscriptionFacade {

    private final SubscriptionService subscriptionService;

    @Override
    @Transactional
    public SubscriptionViewDto grantSubscription(Long adminUserId, Long targetUserId, GrantSubscriptionRequest request) {
        validateAdmin(adminUserId);
        return subscriptionService.grantSubscription(targetUserId, adminUserId, request);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionViewDto getMySubscription(Long authUserId) {
        return subscriptionService.getMySubscription(authUserId);
    }

    private void validateAdmin(Long userId) {
        if (userId == null || userId != ADMIN_ID) {
            throw new ResponseStatusException(FORBIDDEN, "only support can grant subscriptions");
        }
    }
}
