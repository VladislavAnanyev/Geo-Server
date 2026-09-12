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

/**
 * Бизнес-логика откликов на групповые планы.
 */
@Service
@RequiredArgsConstructor
public class PlanResponseService {

    private final PlanRepository planRepository;
    private final PlanResponseRepository planResponseRepository;
    private final UserService userService;

    /**
     * Создаёт отклик пользователя на групповой план или возвращает существующий.
     *
     * @param authUserId идентификатор откликающегося пользователя
     * @param planId     идентификатор группового плана
     * @return результат отклика с идентификаторами плана и отклика
     */
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

    /**
     * Возвращает страницу откликов на групповой план.
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор группового плана
     * @param status     фильтр по статусу отклика или {@code null} для всех
     * @param page       номер страницы
     * @param size       размер страницы
     * @return страница откликов на план
     */
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

    /**
     * Одобряет отклик на групповой план.
     *
     * @param authUserId  идентификатор владельца плана
     * @param planId      идентификатор группового плана
     * @param responseId  идентификатор отклика
     * @return одобренный отклик
     */
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

    /**
     * Отклоняет отклик на групповой план.
     *
     * @param authUserId  идентификатор владельца плана
     * @param planId      идентификатор группового плана
     * @param responseId  идентификатор отклика
     * @return отклонённый отклик
     */
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
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "exception.plan.not.found"));
    }

    private PlanResponse loadResponse(Long responseId, Long planId) {
        PlanResponse response = planResponseRepository.findById(responseId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "exception.plan.response.not.found"));
        if (!response.getPlan().getId().equals(planId)) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.response.plan.mismatch");
        }
        validateGroupPlan(response.getPlan());
        return response;
    }

    private void validateGroupPlan(Plan plan) {
        if (!GROUP.equals(plan.getMode())) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.response.group.only");
        }
        if (plan.getDialog() == null) {
            throw new ResponseStatusException(CONFLICT, "exception.plan.group.no.dialog");
        }
    }

    private void validateActiveAndNotExpired(Plan plan) {
        if (!com.example.meetings.plans.model.domain.PlanStatus.ACTIVE.equals(plan.getStatus())
                || !plan.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(CONFLICT, "exception.plan.not.active");
        }
    }

    private void validateNotOwn(Plan plan, Long authUserId) {
        if (plan.getOwner().getUserId().equals(authUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "exception.plan.respond.own.forbidden");
        }
    }

    private void validateOwner(Plan plan, Long authUserId) {
        if (!plan.getOwner().getUserId().equals(authUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "exception.plan.response.manage.forbidden");
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
