package com.example.meetings.plans.service;

import com.example.meetings.plans.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanExpirationJob {

    private final PlanRepository planRepository;

    @Scheduled(fixedDelayString = "${plans.expire-job-delay-ms:60000}")
    @Transactional
    public void expirePlans() {
        int expired = planRepository.expireActivePlans(LocalDateTime.now());
        if (expired > 0) {
            log.info("Expired {} plans", expired);
        }
    }
}

