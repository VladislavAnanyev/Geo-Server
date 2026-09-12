package com.example.meetings.request.facade;

import com.example.meetings.request.model.dto.output.*;

/**
 * Фасад сценариев заявок в друзья.
 */
public interface RequestFacade {

    /**
     * Отправляет заявку в друзья; при встречной заявке сразу принимает её.
     *
     * @param fromUserId     идентификатор отправителя заявки
     * @param toUserId       идентификатор получателя заявки
     * @param messageContent текст сопроводительного сообщения или {@code null}
     */
    void sendRequest(Long fromUserId, Long toUserId, String messageContent);

    /**
     * Принимает заявку в друзья и создаёт диалог между пользователями.
     *
     * @param requestId идентификатор заявки
     * @param userId    идентификатор пользователя, принимающего заявку
     * @return идентификатор диалога между друзьями
     */
    AcceptRequestResult acceptRequest(Long requestId, Long userId);

    /**
     * Скрывает пользователя из рекомендаций после встречи, создавая отклонённую заявку.
     *
     * @param meetingId идентификатор встречи
     * @param userId    идентификатор текущего пользователя
     */
    void doNotShowInRecommendation(Long meetingId, Long userId);

    /**
     * Отклоняет входящую заявку в друзья.
     *
     * @param requestId идентификатор заявки
     * @param userId    идентификатор пользователя, отклоняющего заявку
     */
    void rejectRequest(Long requestId, Long userId);

    /**
     * Возвращает заявки, отправленные текущим пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список исходящих заявок
     */
    GetSentFromUserRequestsResult getSentToUserRequests(Long userId);

    /**
     * Возвращает входящие заявки в друзья для пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список входящих заявок
     */
    GetRequestsToUserResult getRequestsToUser(Long userId);
}
