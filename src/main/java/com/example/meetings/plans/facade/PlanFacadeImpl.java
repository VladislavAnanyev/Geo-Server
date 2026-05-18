package com.example.meetings.plans.facade;

import com.example.meetings.chat.model.CreateGroupModel;
import com.example.meetings.chat.model.SendMessageModel;
import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.chat.service.MessageService;
import com.example.meetings.plans.model.domain.PlanMode;
import com.example.meetings.plans.model.domain.PlanResponseStatus;
import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.model.dto.input.CreateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.input.NearbyPlansRequest;
import com.example.meetings.plans.model.dto.input.ToggleLivePlanRequest;
import com.example.meetings.plans.model.dto.output.*;
import com.example.meetings.plans.service.PlanResponseService;
import com.example.meetings.plans.service.PlanService;
import com.example.meetings.request.facade.RequestFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.meetings.plans.model.domain.PlanLookupMode.RESPOND;
import static com.example.meetings.plans.model.domain.PlanMode.GROUP;
import static com.example.meetings.util.Constants.ADMIN_ID;
import static java.util.List.of;

@Service
@RequiredArgsConstructor
public class PlanFacadeImpl implements PlanFacade {

    public static final String PLAN_RESPONSE_MARKER = "Отклик на план: ";

    private final PlanService planService;
    private final PlanResponseService planResponseService;
    private final RequestFacade requestFacade;
    private final MessageService messageService;

    @Override
    @Transactional
    public PlanViewDto toggleLive(Long authUserId, ToggleLivePlanRequest request) {
        return planService.toggleLive(authUserId, request);
    }

    @Override
    @Transactional
    public PlanViewDto createScheduled(Long authUserId, CreateScheduledPlanRequest request) {
        Dialog groupDialog = null;
        if (GROUP.equals(request.getMode())) {
            groupDialog = messageService.createGroup(
                    new CreateGroupModel()
                            .setAuthUserId(authUserId)
                            .setName("План: " + request.getDescription())
                            .setUsers(of(authUserId, ADMIN_ID))
            );
            String supportNotice = String.format(
                    "Это чат плана: «%s», начало: %s.",
                    request.getTitle().trim(),
                    request.getStartAt());
            messageService.saveMessage(new SendMessageModel()
                    .setSenderId(ADMIN_ID)
                    .setDialogId(groupDialog.getDialogId())
                    .setContent(supportNotice));
        }
        return planService.createScheduled(authUserId, request, groupDialog);
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
    public PlanRespondResult respond(Long authUserId, Long planId) {
        PlanViewDto plan = planService.getPlan(authUserId, planId, RESPOND);
        if (PlanMode.GROUP.equals(plan.getMode())) {
            return planResponseService.respond(authUserId, planId);
        }
        String message = PLAN_RESPONSE_MARKER + plan.getTitle();
        requestFacade.sendRequest(authUserId, plan.getUser().getUserId(), message);
        return new PlanRespondResult().setPlanId(planId);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponseListResult getResponses(Long authUserId, Long planId, PlanResponseStatus status, Integer page, Integer size) {
        return planResponseService.getResponses(authUserId, planId, status, page, size);
    }

    @Override
    @Transactional
    public PlanResponseViewDto approveResponse(Long authUserId, Long planId, Long responseId) {
        PlanResponseViewDto approved = planResponseService.approve(authUserId, planId, responseId);
        PlanViewDto plan = planService.getPlan(authUserId, planId);
        if (PlanMode.GROUP.equals(plan.getMode()) && plan.getDialogId() != null) {
            messageService.addUserToGroupDialog(approved.getUserId(), plan.getDialogId());
        }
        return approved;
    }

    @Override
    @Transactional
    public PlanResponseViewDto rejectResponse(Long authUserId, Long planId, Long responseId) {
        return planResponseService.reject(authUserId, planId, responseId);
    }

    @Override
    @Transactional
    public PlanViewDto patchStatus(Long authUserId, Long planId, PlanStatus status) {
        return planService.patchStatus(authUserId, planId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanListResult getMyPlans(Long authUserId) {
        return planService.getMyPlans(authUserId);
    }

}

