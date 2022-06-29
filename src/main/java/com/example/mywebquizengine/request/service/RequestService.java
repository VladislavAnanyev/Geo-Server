package com.example.mywebquizengine.request.service;

import com.example.mywebquizengine.chat.repository.MessageRepository;
import com.example.mywebquizengine.meeting.repository.MeetingRepository;
import com.example.mywebquizengine.request.RequestFacade;
import com.example.mywebquizengine.request.model.domain.Request;
import com.example.mywebquizengine.request.model.domain.RequestStatus;
import com.example.mywebquizengine.request.model.dto.output.RequestView;
import com.example.mywebquizengine.request.repository.RequestRepository;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RequestFacade requestFacade;

    @Transactional
    public Request createRequest(Long meetingId, Long fromUserId, Long toUserId, Long messageId) {

        Optional<Request> optionalRequest = isPresentMyRequestByMeetingIdAndToUserId(
                meetingId, toUserId
        );

        if (optionalRequest.isPresent()) {
            requestFacade.acceptRequest(optionalRequest.get().getRequestId(), fromUserId);
            return null;
        } else {
            List<Request> allMyPendingRequestsToUser = requestRepository.findBySenderUserIdAndToUserIdAndStatus(
                    fromUserId,
                    toUserId,
                    RequestStatus.PENDING
            );

            if (allMyPendingRequestsToUser.size() != 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already send request to this user");
            }

            Request request = new Request();
            request.setSender(userService.loadUserByUserId(fromUserId));
            request.setTo(userService.loadUserByUserId(toUserId));
            request.setMeeting(meetingRepository.findById(meetingId).get());
            request.setStatus(RequestStatus.PENDING);

            if (messageId != null) {
                request.setMessage(messageRepository.getOne(messageId));
            }

            if (!isPossibleToSendRequest(request.getMeeting().getMeetingId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not send request");
            }

            return requestRepository.save(request);
        }
    }

    public List<RequestView> getSentRequests(Long userId) {
        return requestRepository.findAllBySenderUserIdAndStatus(userId, RequestStatus.PENDING);
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
                if (request.getStatus().equals(RequestStatus.REJECTED) ||
                        request.getStatus().equals(RequestStatus.ACCEPTED)) {
                    isRequestWithStatusRejectedOrAcceptedExist = true;
                    break;
                }
            }
            return sizeOfSentRequest == 0 && !isRequestWithStatusRejectedOrAcceptedExist;
        }
    }

    public ArrayList<RequestView> getMyRequests(Long userId) {
        return requestRepository.findByStatusAndToUserIdOrStatusAndSenderUserId(
                RequestStatus.PENDING, userId, RequestStatus.PENDING, userId
        );
    }

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

    private Optional<Request> isPresentMyRequestByMeetingIdAndToUserId(Long meetingId, Long toUserId) {
        return requestRepository.findAllByMeetingMeetingIdAndStatusAndSenderUserId(
                meetingId,
                RequestStatus.PENDING,
                toUserId
        );
    }
}
