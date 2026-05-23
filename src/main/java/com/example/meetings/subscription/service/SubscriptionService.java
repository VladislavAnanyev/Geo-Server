package com.example.meetings.subscription.service;

import com.example.meetings.subscription.model.domain.SubscriptionSource;
import com.example.meetings.subscription.model.domain.SubscriptionStatus;
import com.example.meetings.subscription.model.domain.UserSubscription;
import com.example.meetings.subscription.model.dto.input.GrantSubscriptionRequest;
import com.example.meetings.subscription.model.dto.output.SubscriptionViewDto;
import com.example.meetings.subscription.repository.UserSubscriptionRepository;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.service.UserRoleService;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import static com.example.meetings.subscription.model.domain.SubscriptionStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserService userService;
    private final UserRoleService userRoleService;

    @Transactional
    public SubscriptionViewDto grantSubscription(Long targetUserId, Long grantedByUserId, GrantSubscriptionRequest request) {
        User user = userService.loadUserByUserId(targetUserId);
        User grantedBy = userService.loadUserByUserId(grantedByUserId);
        LocalDateTime now = LocalDateTime.now();

        UserSubscription subscription = userSubscriptionRepository
                .findFirstByUserUserIdAndStatusAndExpiresAtAfterOrderByExpiresAtDesc(targetUserId, ACTIVE, now)
                .orElse(null);

        LocalDateTime expiresAt = now.plusDays(request.getDurationDays());
        if (subscription != null) {
            LocalDateTime base = subscription.getExpiresAt().isAfter(now) ? subscription.getExpiresAt() : now;
            expiresAt = base.plusDays(request.getDurationDays());
            subscription.setPlanCode(request.getPlanCode().trim())
                    .setExpiresAt(expiresAt)
                    .setGrantedBy(grantedBy)
                    .setSource(SubscriptionSource.MANUAL)
                    .setExternalPaymentRef(trimToNull(request.getExternalPaymentRef()))
                    .setNote(trimToNull(request.getNote()))
                    .setUpdatedAt(now);
        } else {
            subscription = new UserSubscription()
                    .setUser(user)
                    .setStatus(ACTIVE)
                    .setPlanCode(request.getPlanCode().trim())
                    .setStartsAt(now)
                    .setExpiresAt(expiresAt)
                    .setGrantedBy(grantedBy)
                    .setSource(SubscriptionSource.MANUAL)
                    .setExternalPaymentRef(trimToNull(request.getExternalPaymentRef()))
                    .setNote(trimToNull(request.getNote()))
                    .setCreatedAt(now)
                    .setUpdatedAt(now);
        }

        UserSubscription saved = userSubscriptionRepository.save(subscription);
        userRoleService.syncPremiumRole(user, true);
        return toView(saved, true);
    }

    @Transactional(readOnly = true)
    public boolean isPremium(Long userId) {
        return userSubscriptionRepository
                .findFirstByUserUserIdAndStatusAndExpiresAtAfterOrderByExpiresAtDesc(
                        userId, ACTIVE, LocalDateTime.now())
                .isPresent();
    }

    @Transactional(readOnly = true)
    public SubscriptionViewDto getMySubscription(Long userId) {
        return userSubscriptionRepository
                .findFirstByUserUserIdAndStatusAndExpiresAtAfterOrderByExpiresAtDesc(
                        userId, ACTIVE, LocalDateTime.now())
                .map(sub -> toView(sub, true))
                .orElseGet(() -> new SubscriptionViewDto()
                        .setUserId(userId)
                        .setActive(false));
    }

    private SubscriptionViewDto toView(UserSubscription subscription, boolean active) {
        return new SubscriptionViewDto()
                .setSubscriptionId(subscription.getId())
                .setUserId(subscription.getUser().getUserId())
                .setActive(active)
                .setStatus(subscription.getStatus())
                .setPlanCode(subscription.getPlanCode())
                .setStartsAt(subscription.getStartsAt())
                .setExpiresAt(subscription.getExpiresAt());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
