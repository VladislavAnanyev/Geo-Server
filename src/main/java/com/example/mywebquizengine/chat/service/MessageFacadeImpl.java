package com.example.mywebquizengine.chat.service;

import com.example.mywebquizengine.chat.model.domain.Dialog;
import com.example.mywebquizengine.chat.model.domain.Message;
import com.example.mywebquizengine.chat.model.dto.input.Typing;
import com.example.mywebquizengine.chat.model.dto.output.DialogView;
import com.example.mywebquizengine.chat.model.dto.output.LastDialog;
import com.example.mywebquizengine.chat.model.dto.output.MessageView;
import com.example.mywebquizengine.chat.model.dto.output.TypingView;
import com.example.mywebquizengine.common.rabbit.MessageType;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.chat.model.CreateGroupModel;
import com.example.mywebquizengine.chat.model.SendMessageModel;
import com.example.mywebquizengine.common.NotificationService;
import com.example.mywebquizengine.common.utils.ProjectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MessageFacadeImpl implements MessageFacade {

    @Autowired
    private MessageService messageService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ProjectionUtil projectionUtil;

    @Override
    public void sendMessage(SendMessageModel sendMessageModel) {
        Message message = messageService.saveMessage(sendMessageModel);
        MessageView messageView = projectionUtil.parse(message, MessageView.class);
        notificationService.send(messageView, message.getDialog().getUsers(), MessageType.MESSAGE);
    }

    @Override
    public void typingMessage(Long dialogId, Long userId) {
        Typing typing = messageService.typingMessage(dialogId, userId);
        TypingView typingView = projectionUtil.parse(typing, TypingView.class);

        Set<User> users = new HashSet<>(typing.getDialog().getUsers());
        users.removeIf(user -> user.getUserId().equals(userId));
        notificationService.send(typingView, users, MessageType.TYPING);
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageService.setDeletedStatus(messageId, userId);
        MessageView messageDto = projectionUtil.parse(message, MessageView.class);
        notificationService.send(messageDto, message.getDialog().getUsers(), MessageType.DELETE_MESSAGE);
    }

    @Override
    public void editMessage(Long messageId, String content, Long userId) {
        Message message = messageService.editMessage(messageId, content, userId);
        MessageView messageDto = projectionUtil.parse(message, MessageView.class);
        notificationService.send(messageDto, message.getDialog().getUsers(), MessageType.EDIT_MESSAGE);
    }

    @Override
    public DialogView getChatRoom(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId) {
        Dialog dialog = messageService.getDialog(dialogId, page, pageSize, sortBy, userId);
        return projectionUtil.parse(dialog, DialogView.class);
    }

    @Override
    public List<LastDialog> getLastDialogs(Long userId) {
        return messageService.getDialogs(userId);
    }

    @Override
    public Long createGroup(CreateGroupModel model) {
        Dialog dialog = messageService.createGroup(model);
        this.sendMessage(
                new SendMessageModel()
                        .setSenderId(model.getAuthUserId())
                        .setContent("Группа создана")
                        .setDialogId(dialog.getDialogId())
        );
        return dialog.getDialogId();
    }

    @Override
    public Long createDialog(Long firstUserId, Long secondUserId) {
        return messageService.createDialog(firstUserId, secondUserId);
    }
}
