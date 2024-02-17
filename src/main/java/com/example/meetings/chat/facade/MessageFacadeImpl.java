package com.example.meetings.chat.facade;

import com.example.meetings.chat.mapper.DtoMapper;
import com.example.meetings.chat.model.*;
import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.chat.model.domain.Message;
import com.example.meetings.chat.model.dto.input.Typing;
import com.example.meetings.chat.model.dto.output.*;
import com.example.meetings.chat.service.MessageService;
import com.example.meetings.common.service.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

import static com.example.meetings.chat.mapper.DialogDTOMapper.map;
import static com.example.meetings.chat.model.domain.MessageStatus.READ;
import static com.example.meetings.common.rabbit.eventtype.MessageType.*;
import static java.util.stream.Collectors.toSet;

@Component
public class MessageFacadeImpl implements MessageFacade {

    private final MessageService messageService;
    private final EventService eventService;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    public MessageFacadeImpl(MessageService messageService, EventService eventService, @Qualifier("s3Service") FileStorageService fileStorageService, NotificationService notificationService) {
        this.messageService = messageService;
        this.eventService = eventService;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public void sendMessage(SendMessageModel model) {
        Message message = messageService.saveMessage(model);
        eventService.send(DtoMapper.map(message), message.getDialog().getUsers(), MESSAGE);
        notificationService.send(
                "%s %s".formatted(message.getSender().getFirstName(), message.getSender().getLastName()),
                message.getContent(),
                message
        );
    }

    @Override
    public void typingMessage(Long dialogId, Long userId) {
        Typing typing = messageService.typingMessage(dialogId, userId);
        eventService.send(DtoMapper.map(typing), typing.getUsersToSendNotification(), TYPING);
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageService.setDeletedStatus(messageId, userId);
        eventService.send(DtoMapper.map(message), message.getUsersToSendNotification(), DELETE_MESSAGE);
    }

    @Override
    public void editMessage(Long messageId, String content, Long userId) {
        Message message = messageService.editMessage(messageId, content, userId);
        eventService.send(DtoMapper.map(message), message.getUsersToSendNotification(), EDIT_MESSAGE);
    }

    @Override
    public DialogDTO getChatRoom(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId) {
        Dialog dialog = messageService.getDialog(dialogId, page, pageSize, sortBy, userId);
        readMessages(userId, dialogId);

        return map(dialog, dialog.getMessages(), userId);
    }

    @Override
    public GetDialogsResult getLastDialogs(Long userId) {
        return new GetDialogsResult(
                messageService.getDialogs(userId)
        );
    }

    @Override
    public CreateDialogResult createDialog(Long firstUserId, Long secondUserId) {
        return new CreateDialogResult(
                messageService.createDialog(firstUserId, secondUserId)
        );
    }

    @Override
    public void receiveMessages(Long userId, Long dialogId) {
        Dialog dialog = messageService.findDialogById(dialogId);
        List<Message> justNowReceivedMessages = messageService.receiveMessages(dialog.getMessages(), userId);

        if (justNowReceivedMessages.size() > 0) {
            ChangeMessageStatusEvent event = new ChangeMessageStatusEvent()
                    .setDialog(dialog)
                    .setStatus(READ);

            eventService.send(
                    DtoMapper.map(event),
                    event.getUsersToSendNotification(),
                    CHANGE_STATUS
            );
        }
    }

    @Override
    public void readMessages(Long userId, Long dialogId) {
        Dialog dialog = messageService.findDialogById(dialogId);
        if (messageService.readMessages(dialog.getMessages(), userId).size() > 0) {
            ChangeMessageStatusEvent event = new ChangeMessageStatusEvent()
                    .setDialog(dialog)
                    .setStatus(READ);

            eventService.send(
                    DtoMapper.map(event),
                    event.getDialog().getUsers().stream().filter(user -> !user.getUserId().equals(userId)).collect(toSet()),
                    CHANGE_STATUS
            );
        }
    }

    @Override
    public GetDialogAttachmentsResult getAttachments(Long dialogId) {
        return new GetDialogAttachmentsResult(
                messageService.getAttachments(dialogId)
        );
    }

    @Override
    public UploadAttachmentResult store(InputStream inputStream, String originalFilename, String contentType) {
        return new UploadAttachmentResult()
                .setUri(fileStorageService.store(inputStream, originalFilename, contentType));
    }

}
