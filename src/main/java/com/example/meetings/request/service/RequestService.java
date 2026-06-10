package com.example.meetings.request.service;

import com.example.meetings.chat.repository.MessageRepository;
import com.example.meetings.common.exception.GlobalErrorCode;
import com.example.meetings.common.exception.SubscriptionRequiredException;
import com.example.meetings.request.model.domain.Request;
import com.example.meetings.request.model.domain.RequestStatus;
import com.example.meetings.request.model.dto.output.RequestView;
import com.example.meetings.request.repository.RequestRepository;
import com.example.meetings.subscription.service.SubscriptionService;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.meetings.request.model.domain.RequestStatus.*;
import static java.util.List.of;
import static java.util.Objects.isNull;

/**
 * Сервис операций с заявками в друзья между пользователями.
 */
@Service
@RequiredArgsConstructor
public class RequestService {

    private static final List<RequestStatus> OUTGOING_FRIEND_REQUEST_STATUSES = of(PENDING, ACCEPTED);

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final SubscriptionService subscriptionService;

    @Value("${subscription.free.daily-friend-requests-limit:5}")
    private int dailyFriendRequestsLimit;

    /**
     * Проверяет дневной лимит исходящих заявок в друзья для пользователя без премиум-подписки.
     *
     * @param fromUserId идентификатор отправителя заявки
     */
    public void validateOutgoingFriendRequestLimit(Long fromUserId) {
        if (subscriptionService.isPremium(fromUserId)) {
            return;
        }
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long sentToday = requestRepository.countBySender_UserIdAndCreatedAtGreaterThanEqualAndStatusIn(
                fromUserId,
                startOfDay,
                OUTGOING_FRIEND_REQUEST_STATUSES
        );
        if (sentToday >= dailyFriendRequestsLimit) {
            throw new SubscriptionRequiredException(
                    "exception.subscription.friend.requests.daily.limit",
                    GlobalErrorCode.ERROR_SUBSCRIPTION_FRIEND_REQUESTS_DAILY_LIMIT
            );
        }
    }

    /**
     * Создаёт заявку в друзья от одного пользователя к другому.
     *
     * @param fromUserId идентификатор отправителя заявки
     * @param toUserId   идентификатор получателя заявки
     * @param messageId  идентификатор связанного сообщения или {@code null}
     * @param status     начальный статус заявки
     * @return сохранённая заявка
     */
    @Transactional
    public Request createRequest(Long fromUserId, Long toUserId, Long messageId, RequestStatus status) {

        boolean activeRequestExist = requestRepository.existsBySenderUserIdAndToUserIdAndStatus(
                fromUserId,
                toUserId,
                PENDING
        );

        if (activeRequestExist) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already send request to this user");
        }

        Request request = new Request()
                .setSender(userService.loadUserByUserId(fromUserId))
                .setTo(userService.loadUserByUserId(toUserId))
                .setMessage(isNull(messageId) ? null : messageRepository.getOne(messageId))
                .setStatus(status)
                .setCreatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    /**
     * Проверяет, можно ли отправить заявку между двумя пользователями.
     *
     * @param firstUserId  идентификатор первого пользователя
     * @param secondUserId идентификатор второго пользователя
     * @return {@code true}, если отправка заявки разрешена
     */
    public boolean isPossibleToSendRequest(Long firstUserId, Long secondUserId) {
        return true;
    }

    /**
     * Возвращает исходящие заявки пользователя в статусе ожидания.
     *
     * @param userId идентификатор отправителя
     * @return список отправленных заявок
     */
    public List<RequestView> getSentRequests(Long userId) {
        return requestRepository.findAllBySenderUserIdAndStatus(userId, PENDING);
    }

    /**
     * Возвращает входящие и исходящие заявки пользователя в статусе ожидания.
     *
     * @param userId идентификатор пользователя
     * @return список заявок, где пользователь является отправителем или получателем
     */
    public List<RequestView> getRequestsByUser(Long userId) {
        return requestRepository.findByStatusAndToUserIdOrStatusAndSenderUserId(
                PENDING, userId, PENDING, userId
        );
    }

    /**
     * Изменяет статус входящей заявки (принятие или отклонение).
     *
     * @param requestId     идентификатор заявки
     * @param userId        идентификатор получателя заявки
     * @param requestStatus новый статус заявки
     * @return обновлённая заявка
     */
    public Request changeRequestStatus(Long requestId, Long userId, RequestStatus requestStatus) {
        Optional<Request> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Request request = optionalRequest.get();
        if (!request.getTo().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        request.setStatus(requestStatus);

        return requestRepository.save(request);
    }

    /**
     * Ищет встречную заявку в друзья в статусе ожидания.
     *
     * @param fromUserId идентификатор пользователя, отправившего исходную заявку
     * @param toUserId   идентификатор пользователя, получившего исходную заявку
     * @return встречная заявка, если она существует
     */
    public Optional<Request> findMutualRequest(Long fromUserId, Long toUserId) {
        return requestRepository.findBySenderUserIdAndToUserIdAndStatus(toUserId, fromUserId, PENDING);
    }
}
