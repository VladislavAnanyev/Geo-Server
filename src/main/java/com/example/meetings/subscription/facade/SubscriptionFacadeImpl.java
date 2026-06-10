package com.example.meetings.subscription.facade;

import com.example.meetings.subscription.model.dto.input.GrantSubscriptionRequest;
import com.example.meetings.subscription.model.dto.output.SubscriptionViewDto;
import com.example.meetings.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.meetings.util.Constants.ADMIN_ID;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Реализация фасада подписок с проверкой прав администратора.
 */
@Service
@RequiredArgsConstructor
public class SubscriptionFacadeImpl implements SubscriptionFacade {

    private final SubscriptionService subscriptionService;

    /**
     * Выдаёт подписку пользователю от имени администратора.
     *
     * @param adminUserId  идентификатор администратора, выполняющего операцию
     * @param targetUserId идентификатор пользователя-получателя подписки
     * @param request      параметры выдаваемой подписки
     * @return представление выданной подписки
     */
    @Override
    @Transactional
    public SubscriptionViewDto grantSubscription(Long adminUserId, Long targetUserId, GrantSubscriptionRequest request) {
        validateAdmin(adminUserId);
        return subscriptionService.grantSubscription(targetUserId, adminUserId, request);
    }

    /**
     * Возвращает текущую подписку авторизованного пользователя.
     *
     * @param authUserId идентификатор текущего пользователя
     * @return представление подписки пользователя
     */
    @Override
    @Transactional(readOnly = true)
    public SubscriptionViewDto getMySubscription(Long authUserId) {
        return subscriptionService.getMySubscription(authUserId);
    }

    private void validateAdmin(Long userId) {
        if (userId == null || userId != ADMIN_ID) {
            throw new ResponseStatusException(FORBIDDEN, "exception.subscription.grant.forbidden");
        }
    }
}
