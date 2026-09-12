package com.example.meetings.plans.facade;

import com.example.meetings.moderation.TextModerationService;
import com.example.meetings.chat.model.CreateGroupModel;
import com.example.meetings.chat.model.SendMessageModel;
import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.chat.service.MessageService;
import com.example.meetings.plans.model.domain.Plan;
import com.example.meetings.plans.model.domain.PlanMode;
import com.example.meetings.plans.model.domain.PlanResponseStatus;
import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.model.dto.input.CreateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.input.NearbyPlansRequest;
import com.example.meetings.plans.model.dto.input.ToggleLivePlanRequest;
import com.example.meetings.plans.model.dto.input.UpdateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.output.*;
import com.example.meetings.plans.service.PlanResponseService;
import com.example.meetings.plans.service.PlanService;
import com.example.meetings.request.facade.RequestFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.meetings.plans.model.domain.PlanLookupMode.RESPOND;
import static com.example.meetings.plans.model.domain.PlanMode.GROUP;
import static com.example.meetings.util.Constants.ADMIN_ID;
import static com.example.meetings.util.Constants.PLAN_CHAT_MESSAGE_TEMPLATE;
import static com.example.meetings.util.Constants.PLAN_RESPONSE_MARKER;
import static com.example.meetings.util.Constants.PLAN_START_AT_FORMAT;
import static java.util.List.of;

/**
 * Оркестрация планов: модерация текста, чаты групповых планов, заявки в друзья.
 */
@Service
@RequiredArgsConstructor
public class PlanFacadeImpl implements PlanFacade {

    private final PlanService planService;
    private final PlanResponseService planResponseService;
    private final RequestFacade requestFacade;
    private final MessageService messageService;
    private final TextModerationService textModerationService;

    /**
     * Включает или выключает live-план пользователя.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    флаг включения и координаты для live-плана
     * @return представление live-плана или {@code null}, если активного плана не было при выключении
     */
    @Override
    @Transactional
    public PlanViewDto toggleLive(Long authUserId, ToggleLivePlanRequest request) {
        return planService.toggleLive(authUserId, request);
    }

    /**
     * Создаёт запланированный план; для группового — также чат плана.
     *
     * @param authUserId идентификатор владельца плана
     * @param request    параметры запланированного плана
     * @return созданный план
     */
    @Override
    @Transactional
    public PlanViewDto createScheduled(Long authUserId, CreateScheduledPlanRequest request) {
        planService.validateScheduledPlanCreationAccess(authUserId);
        textModerationService.validatePlanText(request.getTitle(), request.getDescription());
        Dialog groupDialog = null;
        if (GROUP.equals(request.getMode())) {
            groupDialog = messageService.createGroup(
                    new CreateGroupModel()
                            .setAuthUserId(authUserId)
                            .setName("План: " + request.getTitle())
                            .setUsers(of(authUserId, ADMIN_ID))
            );
            String supportNotice = String.format(
                    PLAN_CHAT_MESSAGE_TEMPLATE,
                    request.getTitle().trim(),
                    request.getStartAt().format(PLAN_START_AT_FORMAT));
            messageService.saveMessage(new SendMessageModel()
                    .setSenderId(ADMIN_ID)
                    .setDialogId(groupDialog.getDialogId())
                    .setContent(supportNotice));
        }
        return planService.createScheduled(authUserId, request, groupDialog);
    }

    /**
     * Возвращает планы рядом с точкой с учётом фильтров запроса.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    координаты, радиус, тип и временной диапазон
     * @return страница планов поблизости
     */
    @Override
    @Transactional(readOnly = true)
    public PlanListResult getNearby(Long authUserId, NearbyPlansRequest request) {
        return planService.getNearby(authUserId, request);
    }

    /**
     * Возвращает план по идентификатору.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param planId     идентификатор плана
     * @return представление плана для просмотра
     */
    @Override
    @Transactional(readOnly = true)
    public PlanViewDto getPlan(Long authUserId, Long planId) {
        return planService.getPlan(authUserId, planId);
    }

    /**
     * Откликается на план: заявка в друзья или отклик в групповой план.
     *
     * @param authUserId идентификатор откликающегося пользователя
     * @param planId     идентификатор плана
     * @return результат отклика с идентификатором плана
     */
    @Override
    @Transactional
    public PlanRespondResult respond(Long authUserId, Long planId) {
        PlanViewDto plan = planService.getPlan(authUserId, planId, RESPOND);
        if (PlanMode.GROUP.equals(plan.getMode())) {
            return planResponseService.respond(authUserId, planId);
        }
        String message = PLAN_RESPONSE_MARKER + Optional.ofNullable(plan.getTitle()).orElse("");
        requestFacade.sendRequest(authUserId, plan.getUser().getUserId(), message);
        return new PlanRespondResult().setPlanId(planId);
    }

    /**
     * Возвращает отклики на групповой план (только для владельца).
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор группового плана
     * @param status     фильтр по статусу отклика или {@code null} для всех
     * @param page       номер страницы
     * @param size       размер страницы
     * @return страница откликов на план
     */
    @Override
    @Transactional(readOnly = true)
    public PlanResponseListResult getResponses(Long authUserId, Long planId, PlanResponseStatus status, Integer page, Integer size) {
        return planResponseService.getResponses(authUserId, planId, status, page, size);
    }

    /**
     * Одобряет отклик и добавляет пользователя в чат группового плана.
     *
     * @param authUserId  идентификатор владельца плана
     * @param planId      идентификатор группового плана
     * @param responseId  идентификатор отклика
     * @return одобренный отклик
     */
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

    /**
     * Отклоняет отклик на групповой план.
     *
     * @param authUserId  идентификатор владельца плана
     * @param planId      идентификатор группового плана
     * @param responseId  идентификатор отклика
     * @return отклонённый отклик
     */
    @Override
    @Transactional
    public PlanResponseViewDto rejectResponse(Long authUserId, Long planId, Long responseId) {
        return planResponseService.reject(authUserId, planId, responseId);
    }

    /**
     * Меняет статус плана (закрыть или отменить); только владелец.
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор плана
     * @param status     новый статус ({@code CLOSED} или {@code CANCELLED})
     * @return обновлённый план
     */
    @Override
    @Transactional
    public PlanViewDto patchStatus(Long authUserId, Long planId, PlanStatus status) {
        return planService.patchStatus(authUserId, planId, status);
    }

    /**
     * Обновляет поля запланированного плана; только владелец.
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор плана
     * @param request    новые поля запланированного плана
     * @return обновлённый план
     */
    @Override
    @Transactional
    public PlanViewDto updateScheduled(Long authUserId, Long planId, UpdateScheduledPlanRequest request) {
        textModerationService.validatePlanText(request.getTitle(), request.getDescription());
        return planService.updateScheduled(authUserId, planId, request);
    }

    /**
     * Помечает план удалённым; только владелец.
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор плана
     */
    @Override
    @Transactional
    public void deletePlan(Long authUserId, Long planId) {
        Plan plan = planService.deletePlan(authUserId, planId);
        if (plan.getDialog() != null) {
            messageService.hideDialog(plan.getDialog().getDialogId());
        }
    }

    /**
     * Возвращает активные планы текущего пользователя.
     *
     * @param authUserId идентификатор пользователя
     * @return список активных планов пользователя
     */
    @Override
    @Transactional(readOnly = true)
    public PlanListResult getMyPlans(Long authUserId) {
        return planService.getMyPlans(authUserId);
    }

}
