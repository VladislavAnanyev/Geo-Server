package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;
import com.example.mywebquizengine.model.rabbit.FriendType;
import com.example.mywebquizengine.model.request.domain.Request;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.model.chat.domain.Dialog;
import com.example.mywebquizengine.model.chat.domain.MessageStatus;
import com.example.mywebquizengine.model.request.dto.output.RequestView;
import com.example.mywebquizengine.model.rabbit.RabbitMessage;
import com.example.mywebquizengine.model.rabbit.RequestType;
import com.example.mywebquizengine.repos.RequestRepository;
import com.example.mywebquizengine.service.utils.ProjectionUtil;
import com.example.mywebquizengine.service.utils.RabbitUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONValue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private MessageService messageService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private Optional<Request> isPresentMyRequestByMeetingIdAndToUserId(Long meetingId, Long toUserId) {
        return requestRepository.findAllByMeetingMeetingIdAndStatusAndSenderUserId(meetingId, "PENDING", toUserId);
    }

    @Transactional
    public void sendRequest(Request request, Long userId) throws JsonProcessingException {

        Optional<Request> optionalRequest = isPresentMyRequestByMeetingIdAndToUserId(
                request.getMeeting().getMeetingId(), request.getTo().getUserId()
        );

        if (optionalRequest.isPresent()) {
            acceptRequest(optionalRequest.get().getRequestId(), userId);
        } else {

            List<Request> allMyPendingRequestsToUser = requestRepository.findBySenderUserIdAndToUserIdAndStatus(
                    userId,
                    request.getTo().getUserId(),
                    "PENDING"
            );

            if (allMyPendingRequestsToUser.size() != 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already send request to this user");
            }

            request.setSender(userService.loadUserByUserId(userId));
            request.setTo(userService.loadUserByUserId(request.getTo().getUserId()));
            request.setStatus("PENDING");

            if(!isPossibleToSendRequest(request.getMeeting().getMeetingId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not send request");
            }

            Long dialogId = messageService.createDialog(request.getTo().getUserId(), userId);

            Dialog dialog = new Dialog();
            dialog.setDialogId(dialogId);

            if (request.getMessage() != null) {

                request.getMessage().setDialog(dialog);
                request.getMessage().setSender(userService.loadUserByUserIdProxy(userId));
                request.getMessage().setStatus(MessageStatus.DELIVERED);
                request.getMessage().setTimestamp(new Date());

            }

            requestRepository.save(request);

            RequestView requestView = ProjectionUtil.parseToProjection(request, RequestView.class);

            RabbitMessage<RequestView> rabbitMessage = new RabbitMessage<>();
            rabbitMessage.setType(RequestType.REQUEST);
            rabbitMessage.setPayload(requestView);

            String exchangeName = RabbitUtil.getExchangeName(request.getTo().getUserId());

            rabbitTemplate.convertAndSend(exchangeName, "",
                    JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)));
        }
    }

    public List<RequestView> getSentRequests(Long userId) {
        return requestRepository.findAllBySenderUserIdAndStatus(userId, "PENDING");
    }


    public void rejectRequest(Long id, Long userId) {
        Optional<Request> optionalRequest = requestRepository.findById(id);
        if (optionalRequest.isPresent()) {
            Request request = optionalRequest.get();
            if (request.getTo().getUserId().equals(userId)) {
                requestRepository.updateStatus(request.getRequestId(), "REJECTED");
            } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * ты сейчас можешь отправить заявку либо если заявок по этой встрече нет либо если не существует заявок
     * от тебя и не существует заявок со статусом принята или отклонена
     */
    public boolean isPossibleToSendRequest(Long meetingId) {

        ArrayList<Request> allRequests = requestRepository
                .findAllByMeetingMeetingId(
                        meetingId
                );

        if (allRequests.size() == 0) {
            return true;
        } else {
            Long authUserId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
            ArrayList<RequestView> sentRequest = requestRepository.findByMeetingMeetingIdAndSenderUserId(
                    meetingId,
                    authUserId
            );
            int sizeOfSentRequest = sentRequest.size();
            boolean isRequestWithStatusRejectedOrAcceptedExist = false;
            for (Request request : allRequests) {
                if (request.getStatus().equals("REJECTED") || request.getStatus().equals("ACCEPTED")) {
                    isRequestWithStatusRejectedOrAcceptedExist = true;
                    break;
                }
            }
            return sizeOfSentRequest == 0 && !isRequestWithStatusRejectedOrAcceptedExist;
        }
    }

    public ArrayList<RequestView> getMyRequests(Long userId) {
        return requestRepository.findByStatusAndToUserIdOrStatusAndSenderUserId(
                "PENDING", userId, "PENDING", userId
        );
    }

    public Long acceptRequest(Long requestId, Long userId) throws JsonProcessingException {
        User authUser = userService.loadUserByUserId(userId);

        Request request = requestRepository.findById(requestId).get();
        request.setStatus("ACCEPTED");

        authUser.addFriend(request.getSender());
        requestRepository.save(request);

        UserCommonView toView = ProjectionUtil.parseToProjection(request.getTo(), UserCommonView.class);

        RabbitMessage<UserCommonView> rabbitMessage = new RabbitMessage<>();
        rabbitMessage.setType(FriendType.NEW_FRIEND);
        rabbitMessage.setPayload(toView);

        String senderExchangeName = RabbitUtil.getExchangeName(request.getSender().getUserId());
        String toExchangeName = RabbitUtil.getExchangeName(request.getTo().getUserId());
        rabbitTemplate.convertAndSend(senderExchangeName, "",
                JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)));

        UserCommonView senderView = ProjectionUtil.parseToProjection(request.getSender(), UserCommonView.class);
        rabbitMessage.setPayload(senderView);
        rabbitTemplate.convertAndSend(toExchangeName, "",
                JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)));

        return messageService.createDialog(request.getSender().getUserId(), userId);
    }
}
