package com.example.meetings.subscription.repository;

import com.example.meetings.subscription.model.domain.SubscriptionStatus;
import com.example.meetings.subscription.model.domain.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findFirstByUserUserIdAndStatusAndExpiresAtAfterOrderByExpiresAtDesc(
            Long userId,
            SubscriptionStatus status,
            LocalDateTime now
    );
}
