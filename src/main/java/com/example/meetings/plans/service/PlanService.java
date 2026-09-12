package com.example.meetings.plans.service;

import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.util.Constants;
import com.example.meetings.common.exception.SubscriptionRequiredException;
import com.example.meetings.plans.model.domain.Plan;
import com.example.meetings.plans.model.domain.PlanLookupMode;
import com.example.meetings.plans.model.domain.PlanMode;
import com.example.meetings.plans.model.domain.PlanResponseStatus;
import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.model.domain.PlanType;
import com.example.meetings.plans.model.dto.input.CreateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.input.NearbyPlansRequest;
import com.example.meetings.plans.model.dto.input.ToggleLivePlanRequest;
import com.example.meetings.plans.model.dto.input.UpdateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.output.PlanListResult;
import com.example.meetings.plans.model.dto.output.PlanLocationDto;
import com.example.meetings.plans.model.dto.output.PlanUserDto;
import com.example.meetings.plans.model.dto.output.PlanViewDto;
import com.example.meetings.plans.repository.PlanRepository;
import com.example.meetings.plans.repository.PlanResponseRepository;
import com.example.meetings.subscription.service.SubscriptionService;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;

import static com.example.meetings.plans.model.domain.PlanResponseStatus.PENDING;
import static com.example.meetings.plans.model.domain.PlanMode.GROUP;
import static com.example.meetings.plans.model.domain.PlanMode.PERSONAL;
import static com.example.meetings.plans.model.domain.PlanStatus.*;
import static com.example.meetings.plans.model.domain.PlanType.LIVE;
import static com.example.meetings.plans.model.domain.PlanType.SCHEDULED;
import static com.example.meetings.plans.model.domain.PlanLookupMode.RESPOND;
import static com.example.meetings.plans.model.domain.PlanLookupMode.VIEW;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.List.of;
import static org.springframework.http.HttpStatus.*;

/**
 * Бизнес-логика планов: live, запланированные, поиск рядом и изменение владельцем.
 */
@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanResponseRepository planResponseRepository;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Value("${plans.live.ttl-minutes:120}")
    private int liveTtlMinutes;

    @Value("${plans.default-visibility-radius-m:3000}")
    private int defaultVisibilityRadiusM;

    @Value("${plans.radius}")
    private int planRadius;

    /**
     * Включает live-план с координатами или закрывает активный live-план.
     *
     * @param authUserId идентификатор владельца live-плана
     * @param request    флаг включения и координаты
     * @return представление live-плана или {@code null}, если активного плана не было при выключении
     */
    @Transactional
    public PlanViewDto toggleLive(Long authUserId, ToggleLivePlanRequest request) {
        validateToggleLiveRequest(request);
        Plan active = findActiveLivePlan(authUserId);

        if (isDisableLiveRequest(request)) {
            return closeLivePlan(active, authUserId);
        }

        return toView(saveLivePlan(active, authUserId, request), authUserId);
    }

    /**
     * Сохраняет новый запланированный план.
     *
     * @param authUserId  идентификатор владельца плана
     * @param request     параметры запланированного плана
     * @param groupDialog диалог группового плана или {@code null} для личного плана
     * @return сохранённый план
     */
    @Transactional
    public PlanViewDto createScheduled(Long authUserId, CreateScheduledPlanRequest request, Dialog groupDialog) {
        LocalDateTime expires = validateAndResolveScheduledExpiresAt(request);
        PlanMode mode = request.getMode();
        if (GROUP.equals(mode) && groupDialog == null) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.group.requires.dialog");
        }
        Plan plan = buildScheduledPlan(userService.loadUserByUserId(authUserId), request, expires, mode, groupDialog);

        return toView(planRepository.save(plan), authUserId);
    }

    /**
     * Проверяет право пользователя создавать ещё один запланированный план.
     * <p>Для бесплатной подписки доступен только один активный запланированный план.</p>
     *
     * @param authUserId идентификатор пользователя
     */
    @Transactional(readOnly = true)
    public void validateScheduledPlanCreationAccess(Long authUserId) {
        if (subscriptionService.isPremium(authUserId)) {
            return;
        }

        long activeScheduledPlans = planRepository.countByOwnerUserIdAndTypeAndStatusAndDeletedFalseAndExpiresAtAfter(
                authUserId,
                SCHEDULED,
                ACTIVE,
                LocalDateTime.now()
        );

        if (activeScheduledPlans >= 1) {
            throw new SubscriptionRequiredException(
                    "exception.subscription.scheduled.plans.active.limit",
                    Constants.ERROR_SUBSCRIPTION_SCHEDULED_PLANS_ACTIVE_LIMIT
            );
        }
    }

    /**
     * Возвращает активные планы в радиусе с фильтрами типа и времени.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    координаты, радиус, тип и временной диапазон
     * @return страница планов поблизости
     */
    @Transactional(readOnly = true)
    public PlanListResult getNearby(Long authUserId, NearbyPlansRequest request) {
        validateNearbyRequest(request);
        String typeFilter = normalizeTypeFilter(request.getType());
        Page<Plan> plans = planRepository.findNearby(
                request.getLat(),
                request.getLon(),
                planRadius,
                typeFilter == null ? "" : typeFilter,
                getFromTime(request),
                getToTime(request),
                isExcludeOwn(request),
                authUserId,
                LocalDateTime.now(),
                PageRequest.of(resolvePage(request), resolveSize(request))
        );

        return toListResult(plans, authUserId);
    }

    /**
     * Возвращает план для просмотра.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param planId     идентификатор плана
     * @return представление плана
     */
    @Transactional(readOnly = true)
    public PlanViewDto getPlan(Long authUserId, Long planId) {
        return getPlan(authUserId, planId, VIEW);
    }

    /**
     * Возвращает план с учётом режима доступа.
     *
     * @param authUserId  идентификатор текущего пользователя
     * @param planId      идентификатор плана
     * @param lookupMode  режим: просмотр или отклик (с проверкой активности и запрета на свой план)
     * @return представление плана
     */
    @Transactional(readOnly = true)
    public PlanViewDto getPlan(Long authUserId, Long planId, PlanLookupMode lookupMode) {
        Plan plan = loadPlan(planId);
        if (RESPOND.equals(lookupMode)) {
            validatePlanIsActiveForRespond(plan);
            validateNotOwnPlan(plan, authUserId);
        }

        return toView(plan, authUserId);
    }

    /**
     * Меняет статус плана на {@code CLOSED} или {@code CANCELLED}.
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор плана
     * @param status     новый статус
     * @return обновлённый план
     */
    @Transactional
    public PlanViewDto patchStatus(Long authUserId, Long planId, PlanStatus status) {
        validatePatchStatus(status);
        Plan plan = loadPlan(planId);
        validatePlanOwner(plan, authUserId);

        return toView(closePlan(plan, status), authUserId);
    }

    /**
     * Обновляет поля активного запланированного плана.
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор плана
     * @param request    новые поля плана
     * @return обновлённый план
     */
    @Transactional
    public PlanViewDto updateScheduled(Long authUserId, Long planId, UpdateScheduledPlanRequest request) {
        Plan plan = loadPlan(planId);
        validatePlanOwner(plan, authUserId);
        validatePlanIsActiveForEdit(plan);
        if (!SCHEDULED.equals(plan.getType())) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.only.scheduled.updatable");
        }
        plan.setTitle(request.getTitle().trim())
                .setDescription(trimToNull(request.getDescription()))
                .setStartAt(request.getStartAt())
                .setExpiresAt(validateAndResolveScheduledExpiresAt(request))
                .setLocationName(trimToNull(request.getLocationName()))
                .setLat(request.getLat())
                .setLon(request.getLon())
                .setUpdatedAt(LocalDateTime.now());

        return toView(planRepository.save(plan), authUserId);
    }

    /**
     * Помечает план удалённым; активный live-план дополнительно закрывается.
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор плана
     * @return удалённый план
     */
    @Transactional
    public Plan deletePlan(Long authUserId, Long planId) {
        Plan plan = loadPlan(planId);
        validatePlanOwner(plan, authUserId);
        LocalDateTime now = LocalDateTime.now();
        plan.setDeleted(true).setUpdatedAt(now);
        if (LIVE.equals(plan.getType()) && ACTIVE.equals(plan.getStatus())) {
            plan.setStatus(CLOSED).setClosedAt(now);
        }

        return planRepository.save(plan);
    }

    /**
     * Возвращает активные планы пользователя, исключая просроченный live.
     *
     * @param authUserId идентификатор пользователя
     * @return список активных планов пользователя
     */
    @Transactional(readOnly = true)
    public PlanListResult getMyPlans(Long authUserId) {
        LocalDateTime now = LocalDateTime.now();
        List<Plan> plans = planRepository.findMyPlansExcludingStaleLive(authUserId, now);
        return new PlanListResult()
                .setItems(plans.stream().map(p -> toView(p, authUserId)).toList())
                .setPage(0)
                .setSize(plans.size())
                .setTotal(plans.size());
    }

    private Plan loadPlan(Long planId) {
        return planRepository.findByIdAndDeletedFalse(planId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "exception.plan.not.found"));
    }

    private Plan findActiveLivePlan(Long authUserId) {
        return planRepository.findFirstByOwnerUserIdAndTypeAndStatusAndDeletedFalse(authUserId, LIVE, ACTIVE).orElse(null);
    }

    private PlanViewDto closeLivePlan(Plan active, Long authUserId) {
        if (active == null) {
            return null;
        }
        return toView(closePlan(active, CLOSED), authUserId);
    }

    private Plan closePlan(Plan plan, PlanStatus status) {
        LocalDateTime now = LocalDateTime.now();
        plan.setStatus(status).setUpdatedAt(now).setClosedAt(now);
        return planRepository.save(plan);
    }

    private Plan saveLivePlan(Plan active, Long authUserId, ToggleLivePlanRequest request) {
        User owner = userService.loadUserByUserId(authUserId);
        LocalDateTime now = LocalDateTime.now();
        return planRepository.save(upsertLivePlan(active, owner, request, now));
    }

    private Plan upsertLivePlan(Plan active, User owner, ToggleLivePlanRequest request, LocalDateTime now) {
        return applyLiveFields(getOrCreateLivePlan(active, owner, now), request, now);
    }

    private Plan getOrCreateLivePlan(Plan active, User owner, LocalDateTime now) {
        return active == null
                ? new Plan()
                .setOwner(owner)
                .setType(LIVE)
                .setStatus(ACTIVE)
                .setDeleted(false)
                .setCreatedAt(now)
                : active;
    }

    private Plan applyLiveFields(Plan plan, ToggleLivePlanRequest request, LocalDateTime now) {
        return plan.setLat(request.getLat())
                .setLon(request.getLon())
                .setTitle(null)
                .setDescription(null)
                .setStartAt(null)
                .setLocationName(null)
                .setVisibilityRadiusM(defaultVisibilityRadiusM)
                .setExpiresAt(now.plusMinutes(liveTtlMinutes))
                .setUpdatedAt(now)
                .setMode(PERSONAL)
                .setDialog(null)
                .setClosedAt(null);
    }

    private Plan buildScheduledPlan(User owner, CreateScheduledPlanRequest request, LocalDateTime expiresAt, PlanMode mode, Dialog groupDialog) {
        return new Plan()
                .setOwner(owner)
                .setType(SCHEDULED)
                .setStatus(ACTIVE)
                .setMode(mode)
                .setTitle(request.getTitle().trim())
                .setDescription(trimToNull(request.getDescription()))
                .setStartAt(request.getStartAt())
                .setExpiresAt(expiresAt)
                .setLocationName(trimToNull(request.getLocationName()))
                .setLat(request.getLat())
                .setLon(request.getLon())
                .setVisibilityRadiusM(defaultVisibilityRadiusM)
                .setCreatedAt(LocalDateTime.now())
                .setDialog(GROUP.equals(mode) ? groupDialog : null)
                .setUpdatedAt(LocalDateTime.now())
                .setDeleted(false);
    }

    private PlanListResult toListResult(Page<Plan> plans, Long authUserId) {
        return new PlanListResult()
                .setItems(plans.getContent().stream().map(p -> toView(p, authUserId)).toList())
                .setPage(plans.getNumber())
                .setSize(plans.getSize())
                .setTotal(plans.getTotalElements());
    }

    private PlanViewDto toView(Plan plan, Long authUserId) {
        User owner = plan.getOwner();
        return new PlanViewDto()
                .setPlanId(plan.getId())
                .setType(plan.getType())
                .setMode(plan.getMode())
                .setUser(toUserDto(owner))
                .setTitle(plan.getTitle())
                .setDescription(plan.getDescription())
                .setWhenLabel(plan.getStartAt() == null ? "Сейчас" : null)
                .setStartAt(plan.getStartAt())
                .setEtaHint(null)
                .setLocation(toLocationDto(plan))
                .setOwnPlan(Objects.equals(owner.getUserId(), authUserId))
                .setCreatedAt(plan.getCreatedAt())
                .setExpiresAt(plan.getExpiresAt())
                .setDialogId(resolveDialogIdForViewer(plan))
                .setResponseStatus(resolveResponseStatus(plan, authUserId))
                .setPendingResponsesCount(resolvePendingResponsesCount(plan, authUserId));
    }

    private PlanUserDto toUserDto(User owner) {
        return new PlanUserDto()
                .setUserId(owner.getUserId())
                .setFirstName(owner.getFirstName())
                .setAge(calcAge(owner.getBirthDate()))
                .setAvatarUrl(owner.getMainPhoto() == null ? null : owner.getMainPhoto().getUrl());
    }

    private PlanLocationDto toLocationDto(Plan plan) {
        return new PlanLocationDto()
                .setLat(plan.getLat())
                .setLon(plan.getLon())
                .setLocationName(plan.getLocationName());
    }

    private Integer calcAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validateToggleLiveRequest(ToggleLivePlanRequest request) {
        if (!TRUE.equals(request.getEnabled()) && !FALSE.equals(request.getEnabled())) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.enabled.required");
        }
        if (isEnableLiveRequest(request)) {
            validateCoordinates(request.getLat(), request.getLon());
        }
    }

    private LocalDateTime validateAndResolveScheduledExpiresAt(CreateScheduledPlanRequest request) {
        return validateAndResolveScheduledExpiresAt(
                request.getTitle(),
                request.getStartAt(),
                request.getExpiresAt(),
                request.getLat(),
                request.getLon()
        );
    }

    private LocalDateTime validateAndResolveScheduledExpiresAt(UpdateScheduledPlanRequest request) {
        return validateAndResolveScheduledExpiresAt(
                request.getTitle(),
                request.getStartAt(),
                request.getExpiresAt(),
                request.getLat(),
                request.getLon()
        );
    }

    private LocalDateTime validateAndResolveScheduledExpiresAt(String title,
                                                               LocalDateTime startAt,
                                                               LocalDateTime expiresAt,
                                                               Double lat,
                                                               Double lon) {
        if (title == null || title.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.title.required");
        }
        if (startAt == null) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.start_at.required");
        }
        if ((lat == null) != (lon == null)) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.lat_lon.together");
        }
        if (lat != null) {
            validateCoordinates(lat, lon);
        }
        LocalDateTime resolvedExpiresAt = expiresAt == null ? startAt.plusHours(6) : expiresAt;
        if (!startAt.isBefore(resolvedExpiresAt)) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY, "exception.plan.start_before.expires");
        }
        return resolvedExpiresAt;
    }

    private void validateNearbyRequest(NearbyPlansRequest request) {
        validateCoordinates(request.getLat(), request.getLon());
        if (request.getRadiusM() == null || request.getRadiusM() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.radius.positive");
        }
    }

    private void validatePlanIsActiveForRespond(Plan plan) {
        validatePlanIsActive(plan);
    }

    private void validatePlanIsActiveForEdit(Plan plan) {
        validatePlanIsActive(plan);
    }

    private void validatePlanIsActive(Plan plan) {
        if (plan.isDeleted()) {
            throw new ResponseStatusException(NOT_FOUND, "exception.plan.not.found");
        }
        if (!ACTIVE.equals(plan.getStatus()) || !plan.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(CONFLICT, "exception.plan.not.active");
        }
    }

    private void validateNotOwnPlan(Plan plan, Long authUserId) {
        if (Objects.equals(plan.getOwner().getUserId(), authUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "exception.plan.respond.own.forbidden");
        }
    }

    private void validatePatchStatus(PlanStatus status) {
        if (!of(CLOSED, CANCELLED).contains(status)) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.status.invalid");
        }
    }

    private void validatePlanOwner(Plan plan, Long authUserId) {
        if (!Objects.equals(plan.getOwner().getUserId(), authUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "exception.plan.status.change.forbidden");
        }
    }

    private void validateCoordinates(Double lat, Double lon) {
        if (lat == null || lon == null) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.lat_lon.required");
        }
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.coordinates.invalid");
        }
    }

    private String normalizeTypeFilter(String type) {
        if (type == null || type.equalsIgnoreCase("ALL")) {
            return null;
        }
        try {
            return PlanType.valueOf(type.toUpperCase()).name();
        } catch (Exception e) {
            throw new ResponseStatusException(BAD_REQUEST, "exception.plan.type.invalid");
        }
    }

    private boolean isEnableLiveRequest(ToggleLivePlanRequest request) {
        return TRUE.equals(request.getEnabled());
    }

    private boolean isDisableLiveRequest(ToggleLivePlanRequest request) {
        return FALSE.equals(request.getEnabled());
    }

    private boolean isExcludeOwn(NearbyPlansRequest request) {
        return request.getExcludeOwn() == null || request.getExcludeOwn();
    }

    private int resolvePage(NearbyPlansRequest request) {
        return request.getPage() == null ? 0 : Math.max(0, request.getPage());
    }

    private int resolveSize(NearbyPlansRequest request) {
        return request.getSize() == null ? 20 : Math.min(Math.max(1, request.getSize()), 100);
    }

    private Long resolveDialogIdForViewer(Plan plan) {
        if (!GROUP.equals(plan.getMode())) {
            return null;
        }
        return plan.getDialog() == null ? null : plan.getDialog().getDialogId();
    }

    private PlanResponseStatus resolveResponseStatus(Plan plan, Long authUserId) {
        if (!GROUP.equals(plan.getMode()) || Objects.equals(plan.getOwner().getUserId(), authUserId)) {
            return null;
        }
        return planResponseRepository.findByPlanIdAndUserUserId(plan.getId(), authUserId)
                .map(com.example.meetings.plans.model.domain.PlanResponse::getStatus)
                .orElse(null);
    }

    private Long resolvePendingResponsesCount(Plan plan, Long authUserId) {
        if (!GROUP.equals(plan.getMode()) || !Objects.equals(plan.getOwner().getUserId(), authUserId)) {
            return null;
        }
        return planResponseRepository.countByPlanIdAndStatus(plan.getId(), PENDING);
    }

    @NotNull
    private LocalDateTime getToTime(NearbyPlansRequest request) {
        return request.getToTime() != null
                ? request.getToTime()
                : (request.getFromTime() != null ? request.getFromTime().plusMonths(3) : LocalDateTime.now().plusYears(10));
    }

    @NotNull
    private LocalDateTime getFromTime(NearbyPlansRequest request) {
        return request.getFromTime() != null
                ? request.getFromTime()
                : LocalDateTime.now().minusYears(10);
    }
}

