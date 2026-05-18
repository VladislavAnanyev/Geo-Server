package com.example.meetings.plans.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.plans.facade.PlanFacade;
import com.example.meetings.plans.model.domain.PlanResponseStatus;
import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.model.dto.input.*;
import com.example.meetings.plans.model.dto.output.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiPlansController {

    private final PlanFacade planFacade;

    @ApiOperation("Включить/выключить live план")
    @PostMapping("/plans/live/toggle")
    public PlanResponse toggleLive(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                   @RequestBody @Valid ToggleLivePlanRequest request) {
        return new PlanResponse(planFacade.toggleLive(authUser.getUserId(), request));
    }

    @ApiOperation("Создать запланированный план")
    @PostMapping("/plans")
    public PlanResponse createScheduled(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                        @RequestBody @Valid CreateScheduledPlanRequest request) {
        return new PlanResponse(planFacade.createScheduled(authUser.getUserId(), request));
    }

    @ApiOperation("Получить nearby-планы")
    @GetMapping("/plans/nearby")
    public PlanListResponse getNearby(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                      @ModelAttribute NearbyPlansRequest request) {
        return new PlanListResponse(planFacade.getNearby(authUser.getUserId(), request));
    }

    @ApiOperation("Получить план по id")
    @GetMapping("/plans/{planId}")
    public PlanResponse getPlan(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser, @PathVariable Long planId) {
        return new PlanResponse(planFacade.getPlan(authUser.getUserId(), planId));
    }

    @ApiOperation("Откликнуться на план")
    @PostMapping("/plans/{planId}/respond")
    public PlanRespondResponse respond(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                       @PathVariable Long planId) {
        return new PlanRespondResponse(planFacade.respond(authUser.getUserId(), planId));
    }

    @ApiOperation("Список откликов на групповой план (owner)")
    @GetMapping("/plans/{planId}/responses")
    public PlanResponseListResponse getResponses(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                                 @PathVariable Long planId,
                                                 @RequestParam(required = false) PlanResponseStatus status,
                                                 @RequestParam(required = false, defaultValue = "0") Integer page,
                                                 @RequestParam(required = false, defaultValue = "20") Integer size) {
        return new PlanResponseListResponse(planFacade.getResponses(authUser.getUserId(), planId, status, page, size));
    }

    @ApiOperation("Одобрить отклик на групповой план")
    @PostMapping("/plans/{planId}/responses/{responseId}/approve")
    public PlanResponseActionResponse approveResponse(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                                      @PathVariable Long planId,
                                                      @PathVariable Long responseId) {
        return new PlanResponseActionResponse(planFacade.approveResponse(authUser.getUserId(), planId, responseId));
    }

    @ApiOperation("Отклонить отклик на групповой план")
    @PostMapping("/plans/{planId}/responses/{responseId}/reject")
    public PlanResponseActionResponse rejectResponse(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                                     @PathVariable Long planId,
                                                     @PathVariable Long responseId) {
        return new PlanResponseActionResponse(planFacade.rejectResponse(authUser.getUserId(), planId, responseId));
    }

    @ApiOperation("Обновить статус плана владельцем")
    @PatchMapping("/plans/{planId}/status")
    public PlanResponse patchStatus(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                    @PathVariable Long planId,
                                    @RequestBody @Valid PatchPlanStatusRequest request) {
        try {
            return new PlanResponse(planFacade.patchStatus(authUser.getUserId(), planId, PlanStatus.valueOf(request.getStatus().toUpperCase())));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be CLOSED or CANCELLED");
        }
    }

    @ApiOperation("Мои планы")
    @GetMapping("/plans/me")
    public PlanListResponse getMyPlans(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        return new PlanListResponse(planFacade.getMyPlans(authUser.getUserId()));
    }
}

