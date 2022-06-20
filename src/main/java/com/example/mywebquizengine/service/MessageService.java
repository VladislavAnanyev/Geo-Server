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
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.repos.DialogRepository;
import com.example.mywebquizengine.repos.MessageRepository;
import com.example.mywebquizengine.service.model.SendMessageModel;
import com.example.mywebquizengine.service.utils.ProjectionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@Service
@Validated
public class MessageService {

    private final MessageRepository messageRepository;
    private final DialogRepository dialogRepository;
    private final UserService userService;
    private final ProjectionUtil projectionUtil;
    private final RealTimeEventSender realTimeEventSender;
    private final MessageFactory messageFactory;
    @Value("${hostname}")
    private String hostname;

    public MessageService(MessageRepository messageRepository, DialogRepository dialogRepository,
                          UserService userService, ProjectionUtil projectionUtil, RealTimeEventSender sender,
                          MessageFactory messageFactory) {
        this.messageRepository = messageRepository;
        this.dialogRepository = dialogRepository;
        this.userService = userService;
        this.projectionUtil = projectionUtil;
        this.realTimeEventSender = sender;
        this.messageFactory = messageFactory;
    }

    public Long createDialog(Long companionUserId, Long authUserId) {
        if (authUserId.equals(companionUserId)) {
            throw new IllegalArgumentException("You can not create dialog with yourself");
        }

        Long dialogId = dialogRepository.findDialogBetweenUsers(companionUserId, authUserId);
        if (dialogId != null) {
            return dialogId;
        } else {
            Dialog dialog = new Dialog();
            dialog.addUser(userService.loadUserByUserId(companionUserId));
            dialog.addUser(userService.loadUserByUserId(authUserId));
            dialogRepository.save(dialog);
            return dialog.getDialogId();
        }
    }

    public List<LastDialog> getDialogs(Long userId) {
        return messageRepository.getLastDialogs(userId);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = findMessageById(messageId);
        if (!isCanModifyMessage(message.getSender().getUserId(), userId)) {
            throw new SecurityException("Вы не можете удалить это сообщение");
        }
        message.setStatus(MessageStatus.DELETED);
        MessageView messageDto = projectionUtil.parseToProjection(message, MessageView.class);
        realTimeEventSender.send(messageDto, message.getDialog().getUsers(), MessageType.DELETE_MESSAGE);
    }

    @Transactional
    public DialogView getMessages(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId) {
        Dialog dialog = findDialogById(dialogId);

        if (!isUserContainsInDialog(dialog, userId)) {
            throw new SecurityException("Вы не состоите в этом диалоге");
        }

        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        List<Message> messages = messageRepository
                .findAllByDialog_DialogIdAndStatusNot(
                        dialogId,
                        MessageStatus.DELETED,
                        paging
                ).getContent();

        for (Message message : messages) {
            if (message.getStatus().equals(MessageStatus.DELIVERED) && !message.getSender().getUserId().equals(userId)) {
                message.setStatus(MessageStatus.RECEIVED);
            }
        }

        List<Message> result = new ArrayList<>(messages);
        Collections.reverse(result);
        dialog.setMessages(result);
        return projectionUtil.parseToProjection(dialog, DialogView.class);
    }

    @Transactional
    public void editMessage(Long messageId, String content, Long authUserId) {
        Message message = findMessageById(messageId);

        if (!isCanModifyMessage(message.getSender().getUserId(), authUserId)) {
            throw new SecurityException("Вы не можете изменить это сообщение");
        }

        message.setContent(content);
        message.setStatus(MessageStatus.EDIT);

        MessageView messageDto = projectionUtil.parseToProjection(message, MessageView.class);
        realTimeEventSender.send(messageDto, message.getDialog().getUsers(), MessageType.EDIT_MESSAGE);
    }

    @Transactional
    public void sendMessage(@Valid SendMessageModel sendMessageModel) {
        Dialog dialog = findDialogById(sendMessageModel.getDialogId());
        if (!isUserContainsInDialog(dialog, sendMessageModel.getSenderId())) {
            throw new SecurityException("Вы не состоите в этом диалоге");
        }
        Message message = messageFactory.create(
                sendMessageModel.getContent(), sendMessageModel.getUniqueCode(),
                sendMessageModel.getSenderId(), dialog
        );
        messageRepository.save(message);
        MessageView messageDto = projectionUtil.parseToProjection(message, MessageView.class);
        realTimeEventSender.send(messageDto, dialog.getUsers(), MessageType.MESSAGE);
    }

    public Long createGroup(CreateGroupModel model) {
        Dialog dialog = new Dialog();
        model.getUsers().forEach(userId -> dialog.addUser(userService.loadUserByUserId(userId)));

        if (!isUserContainsInDialog(dialog, model.getAuthUserId())) {
            throw new SecurityException("Вы не состоите в этом диалоге");
        }

        if (model.getName() == null || model.getName().equals("")) {
            dialog.setName("Конференция");
        } else {
            dialog.setName(model.getName());
        }

        Message message = messageFactory.create("Группа создана", null,
                model.getAuthUserId(),
                dialog
        );
        dialog.setMessages(Collections.singletonList(message));
        dialog.setImage(hostname + "/img/default.jpg");
        dialogRepository.save(dialog);

        MessageView messageDto = projectionUtil.parseToProjection(message, MessageView.class);
        realTimeEventSender.send(messageDto, dialog.getUsers(), MessageType.MESSAGE);
        return dialog.getDialogId();
    }

    @Transactional
    public void typingMessage(Long dialogId, Long userId) {
        Dialog dialog = findDialogById(dialogId);
        User authUser = userService.loadUserByUserId(userId);

        if (!isUserContainsInDialog(dialog, userId)) {
            throw new SecurityException("Вы не состоите в этом диалоге");
        }

        Typing typing = new Typing();
        typing.setUser(authUser);
        typing.setDialogId(dialogId);
        TypingView typingView = projectionUtil.parseToProjection(typing, TypingView.class);

        Set<User> users = new HashSet<>(dialog.getUsers());
        users.remove(authUser);
        realTimeEventSender.send(typingView, users, MessageType.TYPING);
    }

    private Dialog findDialogById(Long dialogId) {
        Optional<Dialog> dialog = dialogRepository.findById(dialogId);
        if (dialog.isPresent()) {
            return dialog.get();
        } else {
            throw new EntityNotFoundException("Dialog not found");
        }
    }

    private Message findMessageById(Long messageId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent()) {
            return message.get();
        } else {
            throw new EntityNotFoundException("Message not found");
        }
    }

    private boolean isCanModifyMessage(Long senderId, Long userId) {
        return senderId.equals(userId);
    }

    private boolean isUserContainsInDialog(Dialog dialog, Long userId) {
        return dialog.getUsers().stream().anyMatch(user -> user.getUserId().equals(userId));
    }
}
