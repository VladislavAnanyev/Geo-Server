package com.example.mywebquizengine.chat.facade;

import com.example.mywebquizengine.chat.model.CreateGroupModel;
import com.example.mywebquizengine.chat.model.SendMessageModel;
import com.example.mywebquizengine.chat.model.domain.Dialog;
import com.example.mywebquizengine.chat.model.domain.Message;
import com.example.mywebquizengine.chat.model.domain.MessageStatus;
import com.example.mywebquizengine.chat.model.dto.input.Typing;
import com.example.mywebquizengine.chat.model.dto.output.DialogView;
import com.example.mywebquizengine.chat.model.dto.output.LastDialog;
import com.example.mywebquizengine.chat.model.dto.output.MessageView;
import com.example.mywebquizengine.chat.model.dto.output.TypingView;
import com.example.mywebquizengine.chat.service.MessageService;
import com.example.mywebquizengine.chat.service.NewLastDialog;
import com.example.mywebquizengine.common.service.NotificationService;
import com.example.mywebquizengine.common.rabbit.MessageType;
import com.example.mywebquizengine.common.utils.ProjectionUtil;
import com.example.mywebquizengine.chat.model.ChangeMessageStatusEvent;
import com.example.mywebquizengine.chat.model.FileResponse;
import com.example.mywebquizengine.common.rabbit.ChangeMessageStatusEventView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
        notificationService.send(messageView, message.getUsersToSendNotification(), MessageType.MESSAGE);
    }

    @Override
    public void typingMessage(Long dialogId, Long userId) {
        Typing typing = messageService.typingMessage(dialogId, userId);
        TypingView typingView = projectionUtil.parse(typing, TypingView.class);
        notificationService.send(typingView, typing.getUsersToSendNotification(), MessageType.TYPING);
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageService.setDeletedStatus(messageId, userId);
        MessageView messageDto = projectionUtil.parse(message, MessageView.class);
        notificationService.send(messageDto, message.getUsersToSendNotification(), MessageType.DELETE_MESSAGE);
    }

    @Override
    public void editMessage(Long messageId, String content, Long userId) {
        Message message = messageService.editMessage(messageId, content, userId);
        MessageView messageDto = projectionUtil.parse(message, MessageView.class);
        notificationService.send(messageDto, message.getUsersToSendNotification(), MessageType.EDIT_MESSAGE);
    }

    @Override
    public DialogView getChatRoom(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId) {
        Dialog dialog = messageService.getDialog(dialogId, page, pageSize, sortBy, userId);
        List<Message> justNowReadMessages = messageService.readMessages(dialog.getMessages(), userId);

        if (justNowReadMessages.size() > 0) {
            ChangeMessageStatusEvent event = new ChangeMessageStatusEvent()
                    .setDialog(dialog)
                    .setStatus(MessageStatus.READ);

            ChangeMessageStatusEventView changeMessageStatusEventView = projectionUtil.parse(
                    event,
                    ChangeMessageStatusEventView.class
            );

            notificationService.send(
                    changeMessageStatusEventView,
                    event.getUsersToSendNotification(),
                    MessageType.CHANGE_STATUS
            );
        }

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

    @Override
    public void receiveMessages(Long userId, Long dialogId) {
        Dialog dialog = messageService.findDialogById(dialogId);
        List<Message> justNowReceivedMessages = messageService.receiveMessages(dialog.getMessages(), userId);

        if (justNowReceivedMessages.size() > 0) {
            ChangeMessageStatusEvent event = new ChangeMessageStatusEvent()
                    .setDialog(dialog)
                    .setStatus(MessageStatus.READ);

            ChangeMessageStatusEventView changeMessageStatusEventView = projectionUtil.parse(
                    event,
                    ChangeMessageStatusEventView.class
            );

            notificationService.send(
                    changeMessageStatusEventView,
                    event.getUsersToSendNotification(),
                    MessageType.CHANGE_STATUS
            );
        }
    }

    @Override
    public List<FileResponse> getAttachments(Long dialogId) {
        return messageService.getAttachments(dialogId);
    }
}
