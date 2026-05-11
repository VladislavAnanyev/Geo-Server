package com.example.meetings.plans.facade;

import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.model.dto.input.CreateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.input.NearbyPlansRequest;
import com.example.meetings.plans.model.dto.input.ToggleLivePlanRequest;
import com.example.meetings.plans.model.dto.output.PlanListResult;
import com.example.meetings.plans.model.dto.output.PlanRespondResult;
import com.example.meetings.plans.model.dto.output.PlanViewDto;
import com.example.meetings.plans.service.PlanService;
import com.example.meetings.request.facade.RequestFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.meetings.plans.model.domain.PlanLookupMode.RESPOND;

@Service
@RequiredArgsConstructor
public class PlanFacadeImpl implements PlanFacade {

    public static final String PLAN_RESPONSE_MARKER = "[PLAN_RESPONSE]";

    private final PlanService planService;
    private final RequestFacade requestFacade;

    @Override
    @Transactional
    public PlanViewDto toggleLive(Long authUserId, ToggleLivePlanRequest request) {
        return planService.toggleLive(authUserId, request);
    }

    @Override
    @Transactional
    public PlanViewDto createScheduled(Long authUserId, CreateScheduledPlanRequest request) {
        return planService.createScheduled(authUserId, request);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanListResult getNearby(Long authUserId, NearbyPlansRequest request) {
        return planService.getNearby(authUserId, request);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanViewDto getPlan(Long authUserId, Long planId) {
        return planService.getPlan(authUserId, planId);
    }

    @Override
    @Transactional
    public PlanRespondResult respond(Long authUserId, Long planId, String userMessage) {
        PlanViewDto plan = planService.getPlan(authUserId, planId, RESPOND);
        String message = PLAN_RESPONSE_MARKER + " planId=" + plan.getPlanId() + " type=" + plan.getType().name() + " text=" + sanitizeMessage(userMessage);
        requestFacade.sendRequest(authUserId, plan.getUser().getUserId(), message);
        return new PlanRespondResult().setPlanId(planId);
    }

    @Override
    @Transactional
    public PlanViewDto patchStatus(Long authUserId, Long planId, PlanStatus status) {
        return planService.patchStatus(authUserId, planId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanViewDto getMyActiveLive(Long authUserId) {
        return planService.getMyActiveLive(authUserId);
    }

    private static String sanitizeMessage(String text) {
        String value = trimToNull(text);
        if (value == null) {
            return "";
        }
        String collapsed = value.replaceAll("\\s+", " ");
        return collapsed.length() > 500 ? collapsed.substring(0, 500) : collapsed;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}

