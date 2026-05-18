package com.example.meetings.plans.repository;

import com.example.meetings.plans.model.domain.PlanResponse;
import com.example.meetings.plans.model.domain.PlanResponseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanResponseRepository extends JpaRepository<PlanResponse, Long> {
    Optional<PlanResponse> findByPlanIdAndUserUserId(Long planId, Long userId);

    long countByPlanIdAndStatus(Long planId, PlanResponseStatus status);

    Page<PlanResponse> findByPlanIdAndStatus(Long planId, PlanResponseStatus status, Pageable pageable);

    Page<PlanResponse> findByPlanId(Long planId, Pageable pageable);
}
