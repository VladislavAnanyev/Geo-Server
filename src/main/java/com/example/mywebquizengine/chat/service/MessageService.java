package com.example.mywebquizengine.chat.service;


import com.example.mywebquizengine.chat.model.CreateGroupModel;
import com.example.mywebquizengine.chat.model.SendMessageModel;
import com.example.mywebquizengine.chat.model.domain.Dialog;
import com.example.mywebquizengine.chat.model.domain.Message;
import com.example.mywebquizengine.chat.model.domain.MessageStatus;
import com.example.mywebquizengine.chat.model.dto.input.Typing;
import com.example.mywebquizengine.chat.model.dto.output.LastDialog;
import com.example.mywebquizengine.chat.repository.DialogRepository;
import com.example.mywebquizengine.chat.repository.MessageRepository;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class MessageService {

    private final MessageRepository messageRepository;
    private final DialogRepository dialogRepository;
    private final UserService userService;
    private final MessageFactory messageFactory;
    @Value("${hostname}")
    private String hostname;

    public MessageService(MessageRepository messageRepository, DialogRepository dialogRepository,
                          UserService userService, MessageFactory messageFactory) {
        this.messageRepository = messageRepository;
        this.dialogRepository = dialogRepository;
        this.userService = userService;
        this.messageFactory = messageFactory;
    }

    public Long createDialog(Long firstUserId, Long secondUserId) {
        if (secondUserId.equals(firstUserId)) {
            throw new IllegalArgumentException("You can not create dialog with yourself");
        }

        Long dialogId = dialogRepository.findDialogBetweenUsers(firstUserId, secondUserId);
        if (dialogId != null) {
            return dialogId;
        } else {
            Dialog dialog = new Dialog();
            dialog.addUser(userService.loadUserByUserId(firstUserId));
            dialog.addUser(userService.loadUserByUserId(secondUserId));
            dialogRepository.save(dialog);
            return dialog.getDialogId();
        }
    }

    public List<LastDialog> getDialogs(Long userId) {
        return messageRepository.getLastDialogs(userId);
    }

    @Transactional
    public Message setDeletedStatus(Long messageId, Long userId) {
        Message message = findMessageById(messageId);
        if (!isCanModifyMessage(message.getSender().getUserId(), userId)) {
            throw new SecurityException("Вы не можете удалить это сообщение");
        }
        message.setStatus(MessageStatus.DELETED);
        return message;
    }

    @Transactional
    public Dialog getDialog(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId) {
        Dialog dialog = findDialogById(dialogId);

        if (!isUserContainsInDialog(dialog, userId)) {
            throw new SecurityException("Вы не состоите в этом диалоге");
        }

        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        List<Message> messages = messageRepository.findAllByDialog_DialogIdAndStatusNot(
                dialogId,
                MessageStatus.DELETED,
                paging
        ).getContent();

        for (Message message : messages) {
            if (message.getStatus().equals(MessageStatus.DELIVERED) && !message.getSender().getUserId().equals(userId)) {
                message.setStatus(MessageStatus.RECEIVED);
            }
        }

        List<Message> messageList = new ArrayList<>(messages);
        Collections.reverse(messageList);
        dialog.setMessages(messageList);

        return dialog;
    }

    @Transactional
    public Message editMessage(Long messageId, String content, Long authUserId) {
        Message message = findMessageById(messageId);

        if (!isCanModifyMessage(message.getSender().getUserId(), authUserId)) {
            throw new SecurityException("Вы не можете изменить это сообщение");
        }

        message.setContent(content);
        message.setStatus(MessageStatus.EDIT);
        return message;
    }

    @Transactional
    public Message saveMessage(@Valid SendMessageModel sendMessageModel) {
        Dialog dialog = findDialogById(sendMessageModel.getDialogId());
        if (!isUserContainsInDialog(dialog, sendMessageModel.getSenderId())) {
            throw new SecurityException("Вы не состоите в этом диалоге");
        }
        Message message = messageFactory.create(
                sendMessageModel.getContent(), sendMessageModel.getUniqueCode(),
                sendMessageModel.getSenderId(), dialog
        );
        return messageRepository.save(message);
    }

    public Dialog createGroup(CreateGroupModel model) {
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

        dialog.setImage(hostname + "/img/default.jpg");
        return dialogRepository.save(dialog);
    }

    @Transactional
    public Typing typingMessage(Long dialogId, Long userId) {
        Dialog dialog = findDialogById(dialogId);
        User authUser = userService.loadUserByUserId(userId);

        if (!isUserContainsInDialog(dialog, userId)) {
            throw new SecurityException("Вы не состоите в этом диалоге");
        }

        Typing typing = new Typing();
        typing.setUser(authUser);
        typing.setDialog(dialog);
        return typing;
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
