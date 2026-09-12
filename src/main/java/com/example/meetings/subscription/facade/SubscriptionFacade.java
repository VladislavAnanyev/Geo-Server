package com.example.meetings.subscription.facade;

import com.example.meetings.subscription.model.dto.input.GrantSubscriptionRequest;
import com.example.meetings.subscription.model.dto.output.SubscriptionViewDto;

/**
 * Фасад сценариев API подписок пользователей.
 */
public interface SubscriptionFacade {

    /**
     * Выдаёт подписку пользователю от имени администратора.
     *
     * @param adminUserId  идентификатор администратора, выполняющего операцию
     * @param targetUserId идентификатор пользователя-получателя подписки
     * @param request      параметры выдаваемой подписки
     * @return представление выданной подписки
     */
    SubscriptionViewDto grantSubscription(Long adminUserId, Long targetUserId, GrantSubscriptionRequest request);

    /**
     * Возвращает текущую подписку авторизованного пользователя.
     *
     * @param authUserId идентификатор текущего пользователя
     * @return представление подписки пользователя
     */
    SubscriptionViewDto getMySubscription(Long authUserId);
}
