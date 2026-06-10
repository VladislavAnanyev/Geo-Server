package com.example.meetings.plans.facade;

import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.model.domain.PlanResponseStatus;
import com.example.meetings.plans.model.dto.input.CreateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.input.NearbyPlansRequest;
import com.example.meetings.plans.model.dto.input.ToggleLivePlanRequest;
import com.example.meetings.plans.model.dto.input.UpdateScheduledPlanRequest;
import com.example.meetings.plans.model.dto.output.*;

/**
 * Фасад сценариев API планов (live и запланированные).
 */
public interface PlanFacade {

    /**
     * Включает или выключает live-план пользователя.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    флаг включения и координаты для live-плана
     * @return представление live-плана или {@code null}, если активного плана не было при выключении
     */
    PlanViewDto toggleLive(Long authUserId, ToggleLivePlanRequest request);

    /**
     * Создаёт запланированный план; для группового — также чат плана.
     *
     * @param authUserId идентификатор владельца плана
     * @param request    параметры запланированного плана
     * @return созданный план
     */
    PlanViewDto createScheduled(Long authUserId, CreateScheduledPlanRequest request);

    /**
     * Возвращает планы рядом с точкой с учётом фильтров запроса.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    координаты, радиус, тип и временной диапазон
     * @return страница планов поблизости
     */
    PlanListResult getNearby(Long authUserId, NearbyPlansRequest request);

    /**
     * Возвращает план по идентификатору.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param planId     идентификатор плана
     * @return представление плана для просмотра
     */
    PlanViewDto getPlan(Long authUserId, Long planId);

    /**
     * Откликается на план: заявка в друзья или отклик в групповой план.
     *
     * @param authUserId идентификатор откликающегося пользователя
     * @param planId     идентификатор плана
     * @return результат отклика с идентификатором плана
     */
    PlanRespondResult respond(Long authUserId, Long planId);

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
    PlanResponseListResult getResponses(Long authUserId, Long planId, PlanResponseStatus status, Integer page, Integer size);

    /**
     * Одобряет отклик и добавляет пользователя в чат группового плана.
     *
     * @param authUserId  идентификатор владельца плана
     * @param planId      идентификатор группового плана
     * @param responseId  идентификатор отклика
     * @return одобренный отклик
     */
    PlanResponseViewDto approveResponse(Long authUserId, Long planId, Long responseId);

    /**
     * Отклоняет отклик на групповой план.
     *
     * @param authUserId  идентификатор владельца плана
     * @param planId      идентификатор группового плана
     * @param responseId  идентификатор отклика
     * @return отклонённый отклик
     */
    PlanResponseViewDto rejectResponse(Long authUserId, Long planId, Long responseId);

    /**
     * Меняет статус плана (закрыть или отменить); только владелец.
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор плана
     * @param status     новый статус ({@code CLOSED} или {@code CANCELLED})
     * @return обновлённый план
     */
    PlanViewDto patchStatus(Long authUserId, Long planId, PlanStatus status);

    /**
     * Обновляет поля запланированного плана; только владелец.
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор плана
     * @param request    новые поля запланированного плана
     * @return обновлённый план
     */
    PlanViewDto updateScheduled(Long authUserId, Long planId, UpdateScheduledPlanRequest request);

    /**
     * Помечает план удалённым; только владелец.
     *
     * @param authUserId идентификатор владельца плана
     * @param planId     идентификатор плана
     */
    void deletePlan(Long authUserId, Long planId);

    /**
     * Возвращает активные планы текущего пользователя.
     *
     * @param authUserId идентификатор пользователя
     * @return список активных планов пользователя
     */
    PlanListResult getMyPlans(Long authUserId);
}
