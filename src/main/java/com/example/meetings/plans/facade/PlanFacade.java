package com.example.meetings.plans.facade;

import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.model.domain.PlanResponseStatus;
import com.example.meetings.plans.model.dto.input.CreateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.input.NearbyPlansRequest;
import com.example.meetings.plans.model.dto.input.ToggleLivePlanRequest;
import com.example.meetings.plans.model.dto.input.UpdateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.output.*;

public interface PlanFacade {
    PlanViewDto toggleLive(Long authUserId, ToggleLivePlanRequest request);

    PlanViewDto createScheduled(Long authUserId, CreateScheduledPlanRequest request);

    PlanListResult getNearby(Long authUserId, NearbyPlansRequest request);

    PlanViewDto getPlan(Long authUserId, Long planId);

    PlanRespondResult respond(Long authUserId, Long planId);

    PlanResponseListResult getResponses(Long authUserId, Long planId, PlanResponseStatus status, Integer page, Integer size);

    PlanResponseViewDto approveResponse(Long authUserId, Long planId, Long responseId);

    PlanResponseViewDto rejectResponse(Long authUserId, Long planId, Long responseId);

    PlanViewDto patchStatus(Long authUserId, Long planId, PlanStatus status);

    PlanViewDto updateScheduled(Long authUserId, Long planId, UpdateScheduledPlanRequest request);

    void deletePlan(Long authUserId, Long planId);

    PlanListResult getMyPlans(Long authUserId);
}

