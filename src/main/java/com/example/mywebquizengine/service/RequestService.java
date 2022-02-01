package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.request.Request;
import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.model.chat.Dialog;
import com.example.mywebquizengine.model.chat.MessageStatus;
import com.example.mywebquizengine.model.projection.RequestView;
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
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
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

    @Transactional
    public void sendRequest(Request request, String username) throws JsonProcessingException {

        request.setSender(userService.loadUserByUsername(username));
        request.setTo(userService.loadUserByUsername(request.getTo().getUsername()));
        request.setStatus("PENDING");

        ArrayList<Request> requests = requestRepository
                .findAllByMeetingId(
                        request.getMeeting().getId()
                );

        if (requests.size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вы уже отправляли заявку по этой встрече");
        }

        Long dialogId = messageService.createDialog(request.getTo().getUsername(), username);

        Dialog dialog = new Dialog();
        dialog.setDialogId(dialogId);

        if (request.getMessage() != null) {

            request.getMessage().setDialog(dialog);
            request.getMessage().setSender(userService.loadUserByUsernameProxy(username));
            request.getMessage().setStatus(MessageStatus.DELIVERED);
            request.getMessage().setTimestamp(new Date());

        }

        requestRepository.save(request);

        RequestView requestView = ProjectionUtil.parseToProjection(request, RequestView.class);

        RabbitMessage<RequestView> rabbitMessage = new RabbitMessage<>();
        rabbitMessage.setType(RequestType.REQUEST);
        rabbitMessage.setPayload(requestView);

        String exchangeName = RabbitUtil.getExchangeName(request.getTo().getUsername());

        rabbitTemplate.convertAndSend(exchangeName, "",
                JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)));
    }

    public List<RequestView> getSentRequests(String username) {
        return requestRepository.findAllBySenderUsernameAndStatus(username, "PENDING");
    }


    public void rejectRequest(Long id, String username) {
        Optional<Request> optionalRequest = requestRepository.findById(id);
        if (optionalRequest.isPresent()) {
            Request request = optionalRequest.get();
            if (request.getTo().getUsername().equals(username)) {
                requestRepository.updateStatus(request.getId(), "REJECTED");
            } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    public ArrayList<RequestView> getMyRequests(String username) {
        return requestRepository.findByStatusAndToUsernameOrStatusAndSenderUsername(
                "PENDING", username, "PENDING", username
        );
    }

    public Long acceptRequest(Long requestId, String username) throws JsonProcessingException {
        User authUser = userService.loadUserByUsername(username);

        Request request = requestRepository.findById(requestId).get();
        request.setStatus("ACCEPTED");

        authUser.addFriend(request.getSender());
        requestRepository.save(request);

        RequestView requestView = ProjectionUtil.parseToProjection(request, RequestView.class);

        RabbitMessage<RequestView> rabbitMessage = new RabbitMessage<>();
        rabbitMessage.setType(RequestType.ACCEPT_REQUEST);
        rabbitMessage.setPayload(requestView);

        String senderExchangeName = RabbitUtil.getExchangeName(request.getSender().getUsername());
        String toExchangeName = RabbitUtil.getExchangeName(request.getTo().getUsername());

        rabbitTemplate.convertAndSend(senderExchangeName, "",
                JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)));
        rabbitTemplate.convertAndSend(toExchangeName, "",
                JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)));

        return messageService.createDialog(request.getSender().getUsername(), username);
    }
}
