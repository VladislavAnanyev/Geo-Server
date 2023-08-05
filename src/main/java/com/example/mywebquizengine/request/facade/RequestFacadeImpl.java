package com.example.mywebquizengine.request.facade;

import com.example.mywebquizengine.chat.model.SendMessageModel;
import com.example.mywebquizengine.chat.service.MessageService;
import com.example.mywebquizengine.common.rabbit.eventtype.FriendType;
import com.example.mywebquizengine.common.rabbit.eventtype.RequestType;
import com.example.mywebquizengine.common.service.NotificationService;
import com.example.mywebquizengine.common.utils.ProjectionUtil;
import com.example.mywebquizengine.request.model.dto.output.AcceptRequestResult;
import com.example.mywebquizengine.request.model.dto.output.GetRequestsToUserResult;
import com.example.mywebquizengine.request.model.dto.output.GetSentFromUserRequestsResult;
import com.example.mywebquizengine.request.model.domain.Request;
import com.example.mywebquizengine.request.model.domain.RequestStatus;
import com.example.mywebquizengine.request.model.dto.output.RequestView;
import com.example.mywebquizengine.request.service.RequestService;
import com.example.mywebquizengine.user.model.dto.UserCommonView;
import com.example.mywebquizengine.user.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class RequestFacadeImpl implements RequestFacade {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ProjectionUtil projectionUtil;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FriendService friendService;

    @Override
    @Transactional
    public void sendRequest(Long meetingId, Long fromUserId, Long toUserId, String messageContent) {

        Long messageId = null;
        if (messageContent != null) {
            Long dialogId = messageService.createDialog(toUserId, fromUserId);
            messageId = messageService.saveMessage(
                    new SendMessageModel()
                            .setSenderId(fromUserId)
                            .setContent(messageContent)
                            .setDialogId(dialogId)
                            .setUniqueCode(UUID.randomUUID().toString())
            ).getMessageId();
        }

        Request request = requestService.createRequest(meetingId, fromUserId, toUserId, messageId);
        if (request != null) {
            RequestView requestView = projectionUtil.parse(request, RequestView.class);
            notificationService.send(requestView, request.getUsers(), RequestType.REQUEST);
        }
    }

    @Override
    public AcceptRequestResult acceptRequest(Long requestId, Long userId) {
        Request request = requestService.changeRequestStatus(requestId, userId, RequestStatus.ACCEPTED);
        friendService.makeFriends(request.getSender().getUserId(), request.getTo().getUserId());

        UserCommonView toUserView = projectionUtil.parse(request.getTo(), UserCommonView.class);
        notificationService.send(toUserView, Set.of(request.getSender()), FriendType.NEW_FRIEND);
        UserCommonView senderView = projectionUtil.parse(request.getSender(), UserCommonView.class);
        notificationService.send(senderView, Set.of(request.getTo()), FriendType.NEW_FRIEND);

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
                .setRequests(requestService.getMyRequests(userId));
    }
}
