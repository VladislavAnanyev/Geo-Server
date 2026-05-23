package com.example.meetings.plans.service;

import com.example.meetings.plans.model.domain.Plan;
import com.example.meetings.plans.model.domain.PlanMode;
import com.example.meetings.plans.model.domain.PlanResponse;
import com.example.meetings.plans.model.domain.PlanResponseStatus;
import com.example.meetings.plans.model.dto.output.PlanRespondResult;
import com.example.meetings.plans.model.dto.output.PlanResponseListResult;
import com.example.meetings.plans.model.dto.output.PlanResponseViewDto;
import com.example.meetings.plans.repository.PlanRepository;
import com.example.meetings.plans.repository.PlanResponseRepository;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static com.example.meetings.plans.model.domain.PlanResponseStatus.APPROVED;
import static com.example.meetings.plans.model.domain.PlanResponseStatus.PENDING;
import static com.example.meetings.plans.model.domain.PlanMode.GROUP;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class PlanResponseService {

    private final PlanRepository planRepository;
    private final PlanResponseRepository planResponseRepository;
    private final UserService userService;

    @Transactional
    public PlanRespondResult respond(Long authUserId, Long planId) {
        Plan plan = loadPlan(planId);
        validateGroupPlan(plan);
        validateActiveAndNotExpired(plan);
        validateNotOwn(plan, authUserId);

        PlanResponse existing = planResponseRepository.findByPlanIdAndUserUserId(planId, authUserId).orElse(null);
        if (existing != null) {
            return toResult(planId, existing);
        }

        LocalDateTime now = LocalDateTime.now();
        PlanResponse created = new PlanResponse()
                .setPlan(plan)
                .setUser(userService.loadUserByUserIdProxy(authUserId))
                .setStatus(PENDING)
                .setCreatedAt(now)
                .setUpdatedAt(now);
        PlanResponse saved = planResponseRepository.save(created);
        return toResult(planId, saved);
    }

    @Transactional(readOnly = true)
    public PlanResponseListResult getResponses(Long authUserId, Long planId, PlanResponseStatus status, Integer page, Integer size) {
        Plan plan = loadPlan(planId);
        validateOwner(plan, authUserId);

        int resolvedPage = page == null ? 0 : Math.max(0, page);
        int resolvedSize = size == null ? 20 : Math.min(Math.max(1, size), 100);
        PageRequest pageRequest = PageRequest.of(resolvedPage, resolvedSize);

        Page<PlanResponse> resultPage = status == null
                ? planResponseRepository.findByPlanId(planId, pageRequest)
                : planResponseRepository.findByPlanIdAndStatus(planId, status, pageRequest);

        return new PlanResponseListResult()
                .setItems(resultPage.getContent().stream().map(this::toView).toList())
                .setPage(resultPage.getNumber())
                .setSize(resultPage.getSize())
                .setTotal(resultPage.getTotalElements());
    }

    @Transactional
    public PlanResponseViewDto approve(Long authUserId, Long planId, Long responseId) {
        PlanResponse response = loadResponse(responseId, planId);
        validateOwner(response.getPlan(), authUserId);
        if (APPROVED.equals(response.getStatus())) {
            return toView(response);
        }
        validateActiveAndNotExpired(response.getPlan());
        LocalDateTime now = LocalDateTime.now();
        response.setStatus(APPROVED)
                .setUpdatedAt(now)
                .setDecidedAt(now);
        PlanResponse saved = planResponseRepository.save(response);
        return toView(saved);
    }

    @Transactional
    public PlanResponseViewDto reject(Long authUserId, Long planId, Long responseId) {
        PlanResponse response = loadResponse(responseId, planId);
        validateOwner(response.getPlan(), authUserId);
        LocalDateTime now = LocalDateTime.now();
        response.setStatus(PlanResponseStatus.REJECTED)
                .setUpdatedAt(now)
                .setDecidedAt(now);
        return toView(planResponseRepository.save(response));
    }

    private Plan loadPlan(Long planId) {
        return planRepository.findByIdAndDeletedFalse(planId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "plan not found"));
    }

    private PlanResponse loadResponse(Long responseId, Long planId) {
        PlanResponse response = planResponseRepository.findById(responseId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "plan response not found"));
        if (!response.getPlan().getId().equals(planId)) {
            throw new ResponseStatusException(BAD_REQUEST, "plan response does not belong to plan");
        }
        validateGroupPlan(response.getPlan());
        return response;
    }

    private void validateGroupPlan(Plan plan) {
        if (!GROUP.equals(plan.getMode())) {
            throw new ResponseStatusException(BAD_REQUEST, "responses available only for group plans");
        }
        if (plan.getDialog() == null) {
            throw new ResponseStatusException(CONFLICT, "group plan has no dialog");
        }
    }

    private void validateActiveAndNotExpired(Plan plan) {
        if (!com.example.meetings.plans.model.domain.PlanStatus.ACTIVE.equals(plan.getStatus())
                || !plan.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(CONFLICT, "plan is not active");
        }
    }

    private void validateNotOwn(Plan plan, Long authUserId) {
        if (plan.getOwner().getUserId().equals(authUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "can't respond to own plan");
        }
    }

    private void validateOwner(Plan plan, Long authUserId) {
        if (!plan.getOwner().getUserId().equals(authUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "only owner can manage responses");
        }
    }

    private PlanRespondResult toResult(Long planId, PlanResponse response) {
        return new PlanRespondResult()
                .setPlanId(planId)
                .setResponseId(response.getId())
                .setStatus(response.getStatus().name());
    }

    private PlanResponseViewDto toView(PlanResponse response) {
        return new PlanResponseViewDto()
                .setResponseId(response.getId())
                .setUserId(response.getUser().getUserId())
                .setFirstName(response.getUser().getFirstName())
                .setAvatarUrl(response.getUser().getMainPhoto() == null ? null : response.getUser().getMainPhoto().getUrl())
                .setStatus(response.getStatus())
                .setCreatedAt(response.getCreatedAt())
                .setDecidedAt(response.getDecidedAt());
    }
}
