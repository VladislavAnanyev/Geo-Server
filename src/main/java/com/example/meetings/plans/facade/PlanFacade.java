package com.example.meetings.plans.facade;

import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.model.dto.input.CreateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.input.NearbyPlansRequest;
import com.example.meetings.plans.model.dto.input.ToggleLivePlanRequest;
import com.example.meetings.plans.model.dto.output.PlanListResult;
import com.example.meetings.plans.model.dto.output.PlanRespondResult;
import com.example.meetings.plans.model.dto.output.PlanViewDto;

public interface PlanFacade {
    PlanViewDto toggleLive(Long authUserId, ToggleLivePlanRequest request);

    PlanViewDto createScheduled(Long authUserId, CreateScheduledPlanRequest request);

    PlanListResult getNearby(Long authUserId, NearbyPlansRequest request);

    PlanViewDto getPlan(Long authUserId, Long planId);

    PlanRespondResult respond(Long authUserId, Long planId, String userMessage);

    PlanViewDto patchStatus(Long authUserId, Long planId, PlanStatus status);

    PlanViewDto getMyActiveLive(Long authUserId);
}

