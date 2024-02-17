package com.example.meetings.request.service;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.chat.repository.MessageRepository;
import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.repository.MeetingRepository;
import com.example.meetings.request.model.domain.Request;
import com.example.meetings.request.model.domain.RequestStatus;
import com.example.meetings.request.model.dto.output.RequestView;
import com.example.meetings.request.repository.RequestRepository;
import com.example.meetings.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.example.meetings.request.model.domain.RequestStatus.*;
import static java.util.Objects.isNull;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

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

    @Transactional
    public Request createRequest(Long meetingId, Long fromUserId, Long toUserId, Long messageId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new EntityNotFoundException("Встреча не найдена"));
        if (!isPossibleToSendRequest(meetingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not send request");
        }

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
                .setMeeting(meeting)
                .setMessage(isNull(messageId) ? null : messageRepository.getOne(messageId))
                .setStatus(PENDING);

        return requestRepository.save(request);
    }

    public List<RequestView> getSentRequests(Long userId) {
        return requestRepository.findAllBySenderUserIdAndStatus(userId, PENDING);
    }

    /**
     * ты сейчас можешь отправить заявку либо если заявок по этой встрече нет либо если не существует заявок
     * от тебя и не существует заявок со статусом принята или отклонена
     */
    public boolean isPossibleToSendRequest(Long meetingId) {
        List<Request> allRequests = requestRepository.findAllByMeetingMeetingId(meetingId);

        if (allRequests.size() == 0) {
            return true;
        }

        Long authUserId = ((AuthUserDetails) getContext().getAuthentication().getPrincipal()).getUserId();
        List<RequestView> sentRequest = requestRepository.findByMeetingMeetingIdAndSenderUserId(
                meetingId,
                authUserId
        );

        boolean isRequestWithStatusRejectedOrAcceptedExist = allRequests.stream().anyMatch(
                request -> request.getStatus().equals(REJECTED) || request.getStatus().equals(ACCEPTED)
        );

        return sentRequest.size() == 0 && !isRequestWithStatusRejectedOrAcceptedExist;
    }

    public List<RequestView> getRequestsByUser(Long userId) {
        return requestRepository.findByStatusAndToUserIdOrStatusAndSenderUserId(
                PENDING, userId, PENDING, userId
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

    public Optional<Request> findMutualRequest(Long meetingId, Long toUserId) {
        return requestRepository.findAllByMeetingMeetingIdAndStatusAndSenderUserId(
                meetingId,
                PENDING,
                toUserId
        );
    }
}
