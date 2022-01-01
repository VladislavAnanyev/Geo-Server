package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.chat.Dialog;
import com.example.mywebquizengine.model.chat.MessageStatus;
import com.example.mywebquizengine.model.projection.RequestView;
import com.example.mywebquizengine.model.Request;
import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.repos.DialogRepository;
import com.example.mywebquizengine.repos.RequestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.security.Principal;
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
    private DialogRepository dialogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    @Transactional
    public void sendRequest(Request request, Principal principal) throws JsonProcessingException, ParseException {


        request.setSender(userService.loadUserByUsername(principal.getName()));
        request.setStatus("PENDING");

        ArrayList<Request> requests = requestRepository
                .findAllByMeetingId(
                        request.getMeeting().getId()
                );

        if (requests.size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Long dialogId = messageService.checkDialog(request.getTo().getUsername(), principal.getName());


        Dialog dialog = new Dialog();
        dialog.setDialogId(dialogId);

        if (request.getMessage() != null) {

            request.getMessage().setDialog(dialog);
            request.getMessage().setSender(userService.loadUserByUsernameProxy(principal.getName()));
            request.getMessage().setStatus(MessageStatus.DELIVERED);
            request.getMessage().setTimestamp(new Date());

        }

        requestRepository.save(request);

        RequestView requestView = requestRepository.findRequestById(request.getId());

        JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(objectMapper
                .writeValueAsString(requestView));
        jsonObject.put("type", "REQUEST");

        rabbitTemplate.convertAndSend(request.getTo().getUsername(), "",
                jsonObject);

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

    public Long acceptRequest(Long requestId, String username) {
        User authUser = userService.loadUserByUsername(username);

        Request request = requestRepository.findById(requestId).get();
        request.setStatus("ACCEPTED");

        authUser.addFriend(request.getSender());
        requestRepository.save(request);
        Long dialog_id = messageService.checkDialog(request.getSender().getUsername(), username);

        if (dialog_id == null) {
            Dialog dialog = new Dialog();
            //  Set<User> users = new HashSet<>();
            dialog.addUser(userService.loadUserByUsername(request.getSender().getUsername()));
            dialog.addUser(authUser);

            dialogRepository.save(dialog);
            return dialog.getDialogId();
        } else {
            return dialog_id;
        }
    }


}
