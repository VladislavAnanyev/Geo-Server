package com.example.meetings.request.facade;

import com.example.meetings.chat.model.SendMessageModel;
import com.example.meetings.chat.model.domain.Message;
import com.example.meetings.chat.service.MessageService;
import com.example.meetings.common.rabbit.eventtype.FriendType;
import com.example.meetings.common.rabbit.eventtype.RequestType;
import com.example.meetings.common.service.EventService;
import com.example.meetings.common.service.NotificationService;
import com.example.meetings.common.utils.ProjectionUtil;
import com.example.meetings.request.model.domain.Request;
import com.example.meetings.request.model.domain.RequestStatus;
import com.example.meetings.request.model.dto.output.*;
import com.example.meetings.request.service.RequestService;
import com.example.meetings.user.model.dto.UserCommonView;
import com.example.meetings.user.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.example.meetings.common.utils.Const.REQUEST;
import static com.example.meetings.common.utils.Const.REQUEST_DESCRIPTION;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class RequestFacadeImpl implements RequestFacade {

    private final RequestService requestService;
    private final ProjectionUtil projectionUtil;
    private final EventService eventService;
    private final MessageService messageService;
    private final FriendService friendService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void sendRequest(Long meetingId, Long fromUserId, Long toUserId, String messageContent) {
        Long messageId = ofNullable(messageContent)
                .map(s -> messageService.saveMessage(
                        new SendMessageModel()
                                .setSenderId(fromUserId)
                                .setContent(messageContent)
                                .setDialogId(messageService.createDialog(toUserId, fromUserId))))
                .map(Message::getMessageId)
                .orElse(null);

        Optional<Request> optionalRequest = requestService.findMutualRequest(meetingId, toUserId);
        optionalRequest.ifPresentOrElse(
                request -> this.acceptRequest(request.getRequestId(), fromUserId),
                () -> {
                    Request request = requestService.createRequest(meetingId, fromUserId, toUserId, messageId);
                    RequestView requestView = projectionUtil.parse(request, RequestView.class);
                    eventService.send(requestView, request.getUsers(), RequestType.REQUEST);
                    notificationService.send(REQUEST, REQUEST_DESCRIPTION, request);
                }
        );
    }

    @Override
    public AcceptRequestResult acceptRequest(Long requestId, Long userId) {
        Request request = requestService.changeRequestStatus(requestId, userId, RequestStatus.ACCEPTED);
        friendService.makeFriends(request.getSender().getUserId(), request.getTo().getUserId());

        UserCommonView toUserView = projectionUtil.parse(request.getTo(), UserCommonView.class);
        eventService.send(toUserView, Set.of(request.getSender()), FriendType.NEW_FRIEND);
        UserCommonView senderView = projectionUtil.parse(request.getSender(), UserCommonView.class);
        eventService.send(senderView, Set.of(request.getTo()), FriendType.NEW_FRIEND);

        return new AcceptRequestResult()
                .setDialogId(
                        messageService.createDialog(
                                request.getSender().getUserId(),
                                request.getTo().getUserId()
                        )
                );
    }

    @Override
    public void rejectRequest(Long requestId, Long userId) {
        requestService.changeRequestStatus(requestId, userId, RequestStatus.REJECTED);
    }

    @Override
    public GetSentFromUserRequestsResult getSentToUserRequests(Long userId) {
        return new GetSentFromUserRequestsResult()
                .setRequests(requestService.getSentRequests(userId));
    }

    @Override
    public GetRequestsToUserResult getRequestsToUser(Long userId) {
        return new GetRequestsToUserResult()
                .setRequests(requestService.getRequestsByUser(userId));
    }
}
