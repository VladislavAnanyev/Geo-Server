package com.example.mywebquizengine.service;


import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.model.chat.Dialog;
import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.model.chat.MessageStatus;
import com.example.mywebquizengine.model.projection.DialogView;
import com.example.mywebquizengine.model.projection.LastDialog;
import com.example.mywebquizengine.model.projection.MessageView;
import com.example.mywebquizengine.model.projection.TypingView;
import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.rabbit.RabbitMessage;
import com.example.mywebquizengine.model.chat.Typing;
import com.example.mywebquizengine.repos.DialogRepository;
import com.example.mywebquizengine.repos.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONValue;
import org.springframework.amqp.core.MessagePostProcessor;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@Service
@Validated
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

    public Long createDialog(String username, String authUsername) {

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
        } else throw new IllegalArgumentException("You can not create dialog with myself");


    }

    public String getCompanion(String name, Set<User> users) {


        if (users.size() > 2) {
            return name;
        } else if (users.size() == 2) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Set<User> userSet = new HashSet<>(users);
            userSet.removeIf(user -> user.getUsername().equals(username));
            return userSet.iterator().next().getUsername();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    public String getCompanionAvatar(String image, Set<User> users) {

        if (users.size() > 2) {
            return image;
        } else if (users.size() == 2) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Set<User> userSet = new HashSet<>(users);
            userSet.removeIf(user -> user.getUsername().equals(username));
            return userSet.iterator().next().getPhotos().get(0).getUrl();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
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
                Set<User> userSet = new HashSet<>(users);
                userSet.removeIf(user -> user.getUsername().equals(username));
                return userSet.iterator().next().getUsername();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else throw new EntityNotFoundException("Dialog not found");
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
                Set<User> userSet = new HashSet<>(users);
                userSet.removeIf(user -> user.getUsername().equals(username));
                return userSet.iterator().next().getPhotos().get(0).getUrl();

            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else throw new EntityNotFoundException("Dialog not found");
    }

    public ArrayList<LastDialog> getDialogsForApi(String username) {
        List<LastDialog> messageViews = messageRepository.getDialogsForApi(username);
        return (ArrayList<LastDialog>) messageViews;
    }

    @Transactional
    public void deleteMessage(Long id, String username) throws JsonProcessingException {
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
                throw new SecurityException("You are not author of this message");
            }
        } else {
            throw new EntityNotFoundException("Message with specified id not found");
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
        } else throw new SecurityException("You are not contains in this dialog");

    }

    @Transactional
    public void editMessage(Message newMessage, String username) throws JsonProcessingException {
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
    public void sendMessage(@Valid Message message) throws JsonProcessingException, IllegalAccessException {

        Optional<Dialog> optionalDialog = dialogRepository.findById(message.getDialog().getDialogId());

        if (optionalDialog.isPresent()) {

            Dialog dialog = optionalDialog.get();

            if (dialog.getUsers().stream().anyMatch(user -> user.getUsername()
                    .equals(message.getSender().getUsername()))) {

                User sender = userService.loadUserByUsernameProxy(message.getSender().getUsername());

                message.setSender(sender);
                message.setDialog(dialog);
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
            } else throw new SecurityException("You are not contains in this dialog");
            //else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else throw new EntityNotFoundException("Dialog not found");

    }


    public Long createGroup(Dialog newDialog, String username) throws JsonProcessingException {

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


    // протестить
    @Transactional
    public void typingMessage(@Valid Typing typing) throws JsonProcessingException {

        Optional<Dialog> dialog = dialogRepository.findById(typing.getDialogId());

        if (dialog.isPresent()) {
            Dialog existDialog = dialog.get();

            User authUser = userService.loadUserByUsername(typing.getUser().getUsername());
            typing.setUser(authUser);

            TypingView typingView = ProjectionUtil.parseToProjection(typing, TypingView.class);

            RabbitMessage<TypingView> rabbitMessage = new RabbitMessage<>();
            rabbitMessage.setType(MessageType.TYPING);
            rabbitMessage.setPayload(typingView);

            final MessagePostProcessor messagePostProcessor = message -> {
                message.getMessageProperties().setExpiration(String.valueOf(0));
                message.getMessageProperties().setPriority(0);
                return message;
            };

            for (User user : existDialog.getUsers()) {

                if (!typing.getUser().getUsername().equals(user.getUsername())) {
                    rabbitTemplate.convertAndSend(user.getUsername(), "",
                            JSONValue.parse(objectMapper.writeValueAsString(rabbitMessage)), messagePostProcessor);
                }

            }
        } throw new EntityNotFoundException("Dialog not found");


    }

    @Transactional
    public List<MessageView> getListOfMessages(Long dialogId, Pageable paging) {

        String authName = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Message> messages = messageRepository
                .findAllByDialog_DialogIdAndStatusNot(dialogId, MessageStatus.DELETED, paging)
                .getContent();

        for (Message message : messages) {
            if (message.getStatus().equals(MessageStatus.DELIVERED)
                    && !message.getSender().getUsername().equals(authName)) {
                message.setStatus(MessageStatus.RECEIVED);
            }
        }

        List<MessageView> messageViews = ProjectionUtil.parseToProjectionList(messages, MessageView.class);
        Collections.reverse(messageViews);

        return messageViews;
    }
}
