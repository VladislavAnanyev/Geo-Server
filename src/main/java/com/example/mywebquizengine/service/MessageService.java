package com.example.mywebquizengine.service;


import com.example.mywebquizengine.model.chat.domain.Dialog;
import com.example.mywebquizengine.model.chat.domain.Message;
import com.example.mywebquizengine.model.chat.domain.MessageStatus;
import com.example.mywebquizengine.model.chat.dto.input.Typing;
import com.example.mywebquizengine.model.chat.dto.output.DialogView;
import com.example.mywebquizengine.model.chat.dto.output.LastDialog;
import com.example.mywebquizengine.model.chat.dto.output.MessageView;
import com.example.mywebquizengine.model.chat.dto.output.TypingView;
import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.rabbit.RabbitMessage;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.repos.DialogRepository;
import com.example.mywebquizengine.repos.MessageRepository;
import com.example.mywebquizengine.service.utils.ProjectionUtil;
import com.example.mywebquizengine.service.utils.RabbitUtil;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private MessageRepository messageRepository;
    @Autowired
    private DialogRepository dialogRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${hostname}")
    private String hostname;

    public Long createDialog(Long userId, Long authUserId) {

        if (!authUserId.equals(userId)) {
            Long dialog_id = dialogRepository.findDialogByName(userId, authUserId);

            if (dialog_id == null) {
                Dialog dialog = new Dialog();

                dialog.addUser(userService.loadUserByUserId(userId));
                dialog.addUser(userService.loadUserByUserId(authUserId));

                dialogRepository.save(dialog);
                return dialog.getDialogId();
            } else {
                return dialog_id;
            }
        } else throw new IllegalArgumentException("You can not create dialog with yourself");

    }

    public List<LastDialog> getDialogsForApi(Long userId) {
        return messageRepository.getDialogsForApi(userId);
    }

    @Transactional
    public void deleteMessage(Long id, Long userId) {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            if (message.getSender().getUserId().equals(userId)) {
                message.setStatus(MessageStatus.DELETED);

                MessageView messageDto = ProjectionUtil.parseToProjection(message, MessageView.class);

                RabbitMessage<MessageView> rabbitMessage = new RabbitMessage<>();
                rabbitMessage.setType(MessageType.DELETE_MESSAGE);
                rabbitMessage.setPayload(messageDto);

                for (User user : message.getDialog().getUsers()) {
                    String exchangeName = RabbitUtil.getExchangeName(user.getUserId());
                    rabbitTemplate.convertAndSend(exchangeName, "", rabbitMessage);
                }

            } else {
                throw new SecurityException("You are not author of this message");
            }
        } else {
            throw new EntityNotFoundException("Message with specified id not found");
        }
    }

    @Transactional
    public DialogView getMessages(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialogId);
        if (optionalDialog.isPresent()) {
            Dialog dialog = optionalDialog.get();
            dialog.setPaging(paging);
            if (dialog.getUsers().stream().anyMatch(o -> o.getUserId().equals(userId))) {
                return dialogRepository.findAllDialogByDialogId(dialogId);
            } else throw new SecurityException("You are not contains in this dialog");
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public void editMessage(Message newMessage, Long userId) {
        Optional<Message> optionalMessage = messageRepository.findById(newMessage.getMessageId());
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            if (message.getSender().getUserId().equals(userId)) {
                message.setContent(newMessage.getContent());
                message.setStatus(MessageStatus.EDIT);

                MessageView messageDto = ProjectionUtil.parseToProjection(message, MessageView.class);

                RabbitMessage<MessageView> rabbitMessage = new RabbitMessage<>();
                rabbitMessage.setType(MessageType.EDIT_MESSAGE);
                rabbitMessage.setPayload(messageDto);

                for (User user : message.getDialog().getUsers()) {

                    String exchangeName = RabbitUtil.getExchangeName(user.getUserId());

                    rabbitTemplate.convertAndSend(exchangeName, "", rabbitMessage);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Transactional
    public void sendMessage(@Valid Message message) {

        Optional<Dialog> optionalDialog = dialogRepository.findById(message.getDialog().getDialogId());

        if (optionalDialog.isPresent()) {

            Dialog dialog = optionalDialog.get();

            if (dialog.getUsers().stream().anyMatch(user -> user.getUserId()
                    .equals(message.getSender().getUserId()))) {

                User sender = userService.loadUserByUserIdProxy(message.getSender().getUserId());

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

                    String exchangeName = RabbitUtil.getExchangeName(user.getUserId());

                    rabbitTemplate.convertAndSend(exchangeName, "", rabbitMessage);
                }
            } else throw new SecurityException("You are not contains in this dialog");

        } else throw new EntityNotFoundException("Dialog not found");

    }


    public Long createGroup(Dialog newDialog, Long userId) {

        User authUser = new User();
        authUser.setUserId(userId);
        newDialog.addUser(authUser);

        if (newDialog.getUsers().stream().anyMatch(o -> o.getUserId().equals(userId))) {

            Dialog dialog = new Dialog();

            newDialog.getUsers().forEach(user -> dialog.addUser(userService.loadUserByUserId(user.getUserId())));

            Message message = new Message();
            message.setContent("Группа создана");
            message.setSender(userService.loadUserByUserId(userId));
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
            dialog.setImage(hostname + "/img/default.jpg");
            dialogRepository.save(dialog);

            MessageView messageDto = ProjectionUtil.parseToProjection(message, MessageView.class);

            RabbitMessage<MessageView> rabbitMessage = new RabbitMessage<>();
            rabbitMessage.setType(MessageType.MESSAGE);
            rabbitMessage.setPayload(messageDto);
            for (User user : dialog.getUsers()) {
                String exchangeName = RabbitUtil.getExchangeName(user.getUserId());

                rabbitTemplate.convertAndSend(exchangeName, "", rabbitMessage);
            }

            return dialog.getDialogId();

        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }


    // протестить
    @Transactional
    public void typingMessage(@Valid Typing typing) {

        Optional<Dialog> dialog = dialogRepository.findById(typing.getDialogId());

        if (dialog.isPresent()) {
            Dialog existDialog = dialog.get();

            User authUser = userService.loadUserByUserId(typing.getUser().getUserId());
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

                if (!typing.getUser().getUserId().equals(user.getUserId())) {
                    String exchangeName = RabbitUtil.getExchangeName(user.getUserId());
                    rabbitTemplate.convertAndSend(exchangeName, "", rabbitMessage, messagePostProcessor);

                }

            }
        } else throw new EntityNotFoundException("Dialog not found");


    }

    @Transactional
    public List<MessageView> getListOfMessages(Long dialogId, Pageable paging) {

        String authName = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Message> messages = messageRepository
                .findAllByDialog_DialogIdAndStatusNot(dialogId, MessageStatus.DELETED, paging)
                .getContent();

        for (Message message : messages) {
            if (message.getStatus().equals(MessageStatus.DELIVERED)
                    && !message.getSender().getUserId().equals(authName)) {
                message.setStatus(MessageStatus.RECEIVED);
            }
        }

        List<MessageView> messageViews = ProjectionUtil.parseToProjectionList(messages, MessageView.class);
        Collections.reverse(messageViews);

        return messageViews;
    }
}
