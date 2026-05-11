package com.example.meetings.request.service;

import com.example.meetings.chat.repository.MessageRepository;
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

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepository;

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
                .setStatus(status);

        return requestRepository.save(request);
    }

    public List<RequestView> getSentRequests(Long userId) {
        return requestRepository.findAllBySenderUserIdAndStatus(userId, PENDING);
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

    public Optional<Request> findMutualRequest(Long fromUserId, Long toUserId) {
        return requestRepository.findBySenderUserIdAndToUserIdAndStatus(toUserId, fromUserId, PENDING);
    }
}
