package com.example.mywebquizengine.service;


import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.model.chat.Dialog;
import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.model.chat.MessageStatus;
import com.example.mywebquizengine.model.projection.DialogView;
import com.example.mywebquizengine.model.projection.LastDialog;
import com.example.mywebquizengine.model.projection.MessageView;
import com.example.mywebquizengine.model.projection.TypingView;
import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.rabbit.RabbitMessage;
import com.example.mywebquizengine.model.rabbit.Typing;
import com.example.mywebquizengine.repos.DialogRepository;
import com.example.mywebquizengine.repos.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.boot.json.*;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class MessageService {

    @Autowired
    SessionRegistry sessionRegistry;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private DialogRepository dialogRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${hostname}")
    private String hostname;
    @Autowired
    private ObjectMapper objectMapper;

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }


    public Long checkDialog(String username, String authUsername) {

        if (!authUsername.equals(username)) {
            Long dialog_id = dialogRepository.findDialogByName(username,
                    authUsername);

            if (dialog_id == null) {
                Dialog dialog = new Dialog();

                dialog.addUser(userService.loadUserByUsername(username));
                dialog.addUser(userService.loadUserByUsername(authUsername));

                dialogRepository.save(dialog);
                return dialog.getDialogId();
            } else {
                return dialog_id;
            }
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);


    }

    public String getCompanion(String name, Set<User> users) {


        if (users.size() > 2) {
            return name;
        } else if (users.size() == 2) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            users.removeIf(user -> user.getUsername().equals(username));
            return users.iterator().next().getUsername();
        } else if (users.size() == 1) {
            return users.iterator().next().getUsername();
        } else {
            return "Пустой диалог";
        }

    }

    public String getCompanionAvatar(String image, Set<User> users) {

        if (users.size() > 2) {
            return image;
        } else if (users.size() == 2) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            users.removeIf(user -> user.getUsername().equals(username));
            return users.iterator().next().getPhotos().get(0).getUrl();
        } else if (users.size() == 1) {
            return users.iterator().next().getPhotos().get(0).getUrl();
        } else {
            return "Пустой диалог";
        }

    }

    public String getCompanionForLastDialogs(Long dialog_id) {
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialog_id);
        if (optionalDialog.isPresent()) {
            Dialog dialog = optionalDialog.get();
            Set<User> users = dialog.getUsers();

            if (users.size() > 2) {
                return dialog.getName();
            } else if (users.size() == 2) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                users.removeIf(user -> user.getUsername().equals(username));
                return users.iterator().next().getUsername();
            } else if (users.size() == 1) {
                return users.iterator().next().getUsername();
            } else {
                return "Пустой диалог";
            }
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    public String getCompanionAvatarForLastDialogs(Long dialog_id) {
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialog_id);
        if (optionalDialog.isPresent()) {
            Dialog dialog = optionalDialog.get();
            Set<User> users = dialog.getUsers();

            if (users.size() > 2) {
                return dialog.getImage();
            } else if (users.size() == 2) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                users.removeIf(user -> user.getUsername().equals(username));
                return users.iterator().next().getPhotos().get(0).getUrl();
            } else if (users.size() == 1) {
                return users.iterator().next().getPhotos().get(0).getUrl();
            } else {
                return "Пустой диалог";
            }
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    public ArrayList<LastDialog> getDialogsForApi(String username) {

        List<LastDialog> messageViews = messageRepository.getDialogsForApi(username);

        return (ArrayList<LastDialog>) messageViews;
    }


    @Transactional
    public void deleteMessage(Long id, String username) throws JsonProcessingException, ParseException {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            if (message.getSender().getUsername().equals(username)) {
                message.setStatus(MessageStatus.DELETED);

                MessageView messageDto = ProjectionUtil.parseToProjection(message, MessageView.class);

                RabbitMessage<MessageView> rabbitMessage = new RabbitMessage<>();
                rabbitMessage.setType(MessageType.DELETE_MESSAGE);
                rabbitMessage.setPayload(messageDto);

                for (User user : message.getDialog().getUsers()) {
                    rabbitTemplate.convertAndSend(user.getUsername(), "",
                            JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)));
                }

            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public DialogView getMessages(Long dialogId, Integer page, Integer pageSize, String sortBy, String username) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialogId);

        if (optionalDialog.isPresent()) {
            optionalDialog.get().setPaging(paging);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        DialogView dialog = dialogRepository.findAllDialogByDialogId(dialogId);



        // If user contains in dialog
        if (dialog.getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(username))) {
            return dialog;
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);

    }

    @Transactional
    public void editMessage(Message newMessage, String username) throws JsonProcessingException, ParseException {
        Optional<Message> optionalMessage = messageRepository.findById(newMessage.getId());
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            if (message.getSender().getUsername().equals(username)) {
                message.setContent(newMessage.getContent());
                message.setStatus(MessageStatus.EDIT);

                MessageView messageDto = ProjectionUtil.parseToProjection(message, MessageView.class);

                RabbitMessage<MessageView> rabbitMessage = new RabbitMessage<>();
                rabbitMessage.setType(MessageType.EDIT_MESSAGE);
                rabbitMessage.setPayload(messageDto);

                for (User user : message.getDialog().getUsers()) {
                    rabbitTemplate.convertAndSend(user.getUsername(), "",
                            JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)));
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Transactional
    public void sendMessage(Message message) throws JsonProcessingException, ParseException {

        Dialog dialog = dialogRepository.findById(message.getDialog().getDialogId()).get();
        if (dialog.getUsers().stream().anyMatch(user -> user.getUsername()
                .equals(message.getSender().getUsername()))) {

            User sender = userService.loadUserByUsernameProxy(message.getSender().getUsername());

            message.setSender(sender);
            message.setDialog(dialogRepository.findById(message.getDialog().getDialogId()).get());
            message.setTimestamp(new Date());
            message.setStatus(MessageStatus.DELIVERED);

            messageRepository.save(message);

            MessageView messageDto = ProjectionUtil.parseToProjection(message, MessageView.class);

            RabbitMessage<MessageView> rabbitMessage = new RabbitMessage<>();
            rabbitMessage.setType(MessageType.MESSAGE);
            rabbitMessage.setPayload(messageDto);

            for (User user : dialog.getUsers()) {
                rabbitTemplate.convertAndSend(user.getUsername(), "",
                        JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)));
            }
        }

    }


    public Long createGroup(Dialog newDialog, String username) throws JsonProcessingException, ParseException {

        User authUser = new User();

        authUser.setUsername(username);

        newDialog.addUser(authUser);

        if (newDialog.getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(username))) {

            Dialog dialog = new Dialog();

            newDialog.getUsers().forEach(user -> dialog.addUser(userService.loadUserByUsername(user.getUsername())));

            Message message = new Message();
            message.setContent("Группа создана");
            message.setSender(userService.loadUserByUsername(username));
            message.setStatus(MessageStatus.DELIVERED);
            message.setTimestamp(new Date());
            message.setDialog(dialog);

            dialog.setMessages(new ArrayList<>());
            dialog.getMessages().add(message);


            if (newDialog.getName() == null || newDialog.getName().equals("")) {
                dialog.setName("Конференция");
            } else {
                dialog.setName(newDialog.getName());
            }

            //group.setCreator(userService.getAuthUser(SecurityContextHolder.getContext().getAuthentication()));
            dialog.setImage("https://" + hostname + "/img/default.jpg");
            dialogRepository.save(dialog);

            MessageView messageDto = ProjectionUtil.parseToProjection(message, MessageView.class);

            RabbitMessage<MessageView> rabbitMessage = new RabbitMessage<>();
            rabbitMessage.setType(MessageType.MESSAGE);
            rabbitMessage.setPayload(messageDto);
            for (User user : dialog.getUsers()) {
                rabbitTemplate.convertAndSend(user.getUsername(), "",
                        JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)));
            }

            return dialog.getDialogId();

        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    public DialogView getDialogWithPaging(String dialog_id, Integer page, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());

        dialogRepository.findById(Long.valueOf(dialog_id)).get().setPaging(paging);
        return dialogRepository.findAllDialogByDialogId(Long.valueOf(dialog_id));
    }


    // протестить
    public void typingMessage(Typing typing) throws JsonProcessingException, ParseException {

        Dialog dialog = dialogRepository.findById(typing.getDialogId()).get();

        TypingView typingView = ProjectionUtil.parseToProjection(typing, TypingView.class);

        RabbitMessage<TypingView> rabbitMessage = new RabbitMessage<>();
        rabbitMessage.setType(MessageType.TYPING);
        rabbitMessage.setPayload(typingView);

        MessageProperties props = new MessageProperties();
        props.setExpiration(String.valueOf(0));
        org.springframework.amqp.core.Message rabbitMessageWithTTL =
                new org.springframework.amqp.core.Message(objectMapper.writeValueAsString(rabbitMessage).getBytes(), props);

        for (User user : dialog.getUsers()) {
            rabbitTemplate.convertAndSend(user.getUsername(), "",
                    JSONValue.parse(objectMapper.writeValueAsString(rabbitMessageWithTTL)));
        }
    }
}
