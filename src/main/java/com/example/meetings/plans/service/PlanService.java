package com.example.meetings.plans.service;

import com.example.meetings.plans.model.domain.Plan;
import com.example.meetings.plans.model.domain.PlanLookupMode;
import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.model.domain.PlanType;
import com.example.meetings.plans.model.dto.input.CreateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.input.NearbyPlansRequest;
import com.example.meetings.plans.model.dto.input.ToggleLivePlanRequest;
import com.example.meetings.plans.model.dto.output.PlanListResult;
import com.example.meetings.plans.model.dto.output.PlanLocationDto;
import com.example.meetings.plans.model.dto.output.PlanUserDto;
import com.example.meetings.plans.model.dto.output.PlanViewDto;
import com.example.meetings.plans.repository.PlanRepository;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

import static com.example.meetings.plans.model.domain.PlanStatus.*;
import static com.example.meetings.plans.model.domain.PlanType.LIVE;
import static com.example.meetings.plans.model.domain.PlanType.SCHEDULED;
import static com.example.meetings.plans.model.domain.PlanLookupMode.RESPOND;
import static com.example.meetings.plans.model.domain.PlanLookupMode.VIEW;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.List.of;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final UserService userService;

    @Value("${plans.live.ttl-minutes:120}")
    private int liveTtlMinutes;

    @Value("${plans.default-visibility-radius-m:3000}")
    private int defaultVisibilityRadiusM;

    @Transactional
    public PlanViewDto toggleLive(Long authUserId, ToggleLivePlanRequest request) {
        validateToggleLiveRequest(request);
        Plan active = findActiveLivePlan(authUserId);

        if (isDisableLiveRequest(request)) {
            return closeLivePlan(active, authUserId);
        }

        return toView(saveLivePlan(active, authUserId, request), authUserId);
    }

    @Transactional
    public PlanViewDto createScheduled(Long authUserId, CreateScheduledPlanRequest request) {
        LocalDateTime expiresAt = validateAndResolveScheduledExpiresAt(request);
        User owner = userService.loadUserByUserId(authUserId);
        Plan plan = buildScheduledPlan(owner, request, expiresAt, LocalDateTime.now());
        return toView(planRepository.save(plan), authUserId);
    }

    @Transactional(readOnly = true)
    public PlanListResult getNearby(Long authUserId, NearbyPlansRequest request) {
        validateNearbyRequest(request);
        String typeFilter = normalizeTypeFilter(request.getType());
        Page<Plan> plans = planRepository.findNearby(
                request.getLat(),
                request.getLon(),
                request.getRadiusM(),
                typeFilter,
                request.getFromTime(),
                request.getToTime(),
                isExcludeOwn(request),
                authUserId,
                LocalDateTime.now(),
                PageRequest.of(resolvePage(request), resolveSize(request))
        );
        return toListResult(plans, authUserId);
    }

    @Transactional(readOnly = true)
    public PlanViewDto getPlan(Long authUserId, Long planId) {
        return getPlan(authUserId, planId, VIEW);
    }

    @Transactional(readOnly = true)
    public PlanViewDto getPlan(Long authUserId, Long planId, PlanLookupMode lookupMode) {
        Plan plan = loadPlan(planId);
        if (RESPOND.equals(lookupMode)) {
            validatePlanIsActiveForRespond(plan);
            validateNotOwnPlan(plan, authUserId);
        }
        return toView(plan, authUserId);
    }

    @Transactional
    public PlanViewDto patchStatus(Long authUserId, Long planId, PlanStatus status) {
        validatePatchStatus(status);
        Plan plan = loadPlan(planId);
        validatePlanOwner(plan, authUserId);
        return toView(closePlan(plan, status), authUserId);
    }

    @Transactional(readOnly = true)
    public PlanViewDto getMyActiveLive(Long authUserId) {
        Plan active = findActiveLivePlan(authUserId);
        return active == null ? null : toView(active, authUserId);
    }

    private Plan loadPlan(Long planId) {
        return planRepository.findById(planId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "plan not found"));
    }

    private Plan findActiveLivePlan(Long authUserId) {
        return planRepository.findFirstByOwnerUserIdAndTypeAndStatus(authUserId, LIVE, ACTIVE).orElse(null);
    }

    private PlanViewDto closeLivePlan(Plan active, Long authUserId) {
        if (active == null) {
            return getMyActiveLive(authUserId);
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
        return planRepository.save(upsertLivePlan(active, owner, request, LocalDateTime.now()));
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
                .setClosedAt(null);
    }

    private Plan buildScheduledPlan(User owner, CreateScheduledPlanRequest request, LocalDateTime expiresAt, LocalDateTime now) {
        return new Plan()
                .setOwner(owner)
                .setType(SCHEDULED)
                .setStatus(ACTIVE)
                .setTitle(request.getTitle().trim())
                .setDescription(trimToNull(request.getDescription()))
                .setStartAt(request.getStartAt())
                .setExpiresAt(expiresAt)
                .setLocationName(trimToNull(request.getLocationName()))
                .setLat(request.getLat())
                .setLon(request.getLon())
                .setVisibilityRadiusM(defaultVisibilityRadiusM)
                .setCreatedAt(now)
                .setUpdatedAt(now);
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
                .setUser(toUserDto(owner))
                .setTitle(plan.getTitle())
                .setDescription(plan.getDescription())
                .setWhenLabel(plan.getStartAt() == null ? "Сейчас" : null)
                .setStartAt(plan.getStartAt())
                .setEtaHint(null)
                .setLocation(toLocationDto(plan))
                .setOwnPlan(Objects.equals(owner.getUserId(), authUserId))
                .setCreatedAt(plan.getCreatedAt())
                .setExpiresAt(plan.getExpiresAt());
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
            throw new ResponseStatusException(BAD_REQUEST, "enabled is required");
        }
        if (isEnableLiveRequest(request)) {
            validateCoordinates(request.getLat(), request.getLon());
        }
    }

    private LocalDateTime validateAndResolveScheduledExpiresAt(CreateScheduledPlanRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "title is required");
        }
        if (request.getStartAt() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "startAt is required");
        }
        if ((request.getLat() == null) != (request.getLon() == null)) {
            throw new ResponseStatusException(BAD_REQUEST, "lat and lon must be provided together");
        }
        if (request.getLat() != null) {
            validateCoordinates(request.getLat(), request.getLon());
        }
        LocalDateTime expiresAt = request.getExpiresAt() == null ? request.getStartAt().plusHours(6) : request.getExpiresAt();
        if (!request.getStartAt().isBefore(expiresAt)) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY, "startAt must be before expiresAt");
        }
        return expiresAt;
    }

    private void validateNearbyRequest(NearbyPlansRequest request) {
        validateCoordinates(request.getLat(), request.getLon());
        if (request.getRadiusM() == null || request.getRadiusM() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "radiusM must be > 0");
        }
    }

    private void validatePlanIsActiveForRespond(Plan plan) {
        if (!ACTIVE.equals(plan.getStatus()) || !plan.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(CONFLICT, "plan is not active");
        }
    }

    private void validateNotOwnPlan(Plan plan, Long authUserId) {
        if (Objects.equals(plan.getOwner().getUserId(), authUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "can't respond to own plan");
        }
    }

    private void validatePatchStatus(PlanStatus status) {
        if (!of(CLOSED, CANCELLED).contains(status)) {
            throw new ResponseStatusException(BAD_REQUEST, "status must be CLOSED or CANCELLED");
        }
    }

    private void validatePlanOwner(Plan plan, Long authUserId) {
        if (!Objects.equals(plan.getOwner().getUserId(), authUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "only owner can change plan status");
        }
    }

    private void validateCoordinates(Double lat, Double lon) {
        if (lat == null || lon == null) {
            throw new ResponseStatusException(BAD_REQUEST, "lat and lon are required");
        }
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new ResponseStatusException(BAD_REQUEST, "invalid coordinates");
        }
    }

    private String normalizeTypeFilter(String type) {
        if (type == null || type.equalsIgnoreCase("ALL")) {
            return null;
        }
        try {
            return PlanType.valueOf(type.toUpperCase()).name();
        } catch (Exception e) {
            throw new ResponseStatusException(BAD_REQUEST, "type must be LIVE|SCHEDULED|ALL");
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
}

