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
import static com.example.meetings.util.Constants.ADMIN_ID;
import static java.util.stream.Collectors.toSet;

/**
 * Реализация фасада чата: события RabbitMQ, push-уведомления и хранение вложений.
 */
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

    /**
     * Сохраняет сообщение, рассылает событие участникам диалога и push-уведомление.
     *
     * @param sendMessageModel параметры отправляемого сообщения
     */
    @Override
    @Transactional
    public void sendMessage(SendMessageModel model) {
        Message message = messageService.saveMessage(model);
        if (message == null) {
            return;
        }
        eventService.send(DtoMapper.map(message), message.getDialog().getUsers(), MESSAGE);
        notificationService.send(
                message.getSender().getFirstName(),
                message.getContent(),
                message
        );
    }

    /**
     * Уведомляет участников диалога о наборе текста пользователем.
     *
     * @param dialogId идентификатор диалога
     * @param userId   идентификатор пользователя, который набирает сообщение
     */
    @Override
    public void typingMessage(Long dialogId, Long userId) {
        Typing typing = messageService.typingMessage(dialogId, userId);
        eventService.send(DtoMapper.map(typing), typing.getUsersToSendNotification(), TYPING);
    }

    /**
     * Помечает сообщение удалённым и рассылает событие участникам диалога.
     *
     * @param messageId идентификатор сообщения
     * @param userId    идентификатор пользователя, выполняющего удаление
     */
    @Override
    public void deleteMessage(Long messageId, Long userId) {
        // todo добавить изменение ссылки на последнее сообщение если удаляется последнее
        Message message = messageService.setDeletedStatus(messageId, userId);
        eventService.send(DtoMapper.map(message), message.getUsersToSendNotification(), DELETE_MESSAGE);
    }

    /**
     * Изменяет текст сообщения и рассылает событие участникам диалога.
     *
     * @param messageId идентификатор сообщения
     * @param content   новый текст сообщения
     * @param userId    идентификатор пользователя, выполняющего редактирование
     */
    @Override
    public void editMessage(Long messageId, String content, Long userId) {
        Message message = messageService.editMessage(messageId, content, userId);
        eventService.send(DtoMapper.map(message), message.getUsersToSendNotification(), EDIT_MESSAGE);
    }

    /**
     * Возвращает диалог с постраничным списком сообщений и помечает их прочитанными.
     *
     * @param dialogId идентификатор диалога
     * @param page     номер страницы сообщений
     * @param pageSize размер страницы
     * @param sortBy   поле и направление сортировки сообщений
     * @param userId   идентификатор текущего пользователя
     * @return представление диалога с сообщениями
     */
    @Override
    @Transactional
    public DialogDTO getChatRoom(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId) {
        Dialog dialog = messageService.getDialog(dialogId, page, pageSize, sortBy, userId);
        readMessages(userId, dialogId);

        return map(dialog, dialog.getMessages(), userId);
    }

    /**
     * Возвращает список последних диалогов пользователя.
     *
     * @param userId   идентификатор текущего пользователя
     * @param page     номер страницы
     * @param pageSize размер страницы
     * @return результат со списком диалогов
     */
    @Override
    public GetDialogsResult getLastDialogs(Long userId, Integer page, Integer pageSize) {
        return new GetDialogsResult(messageService.getDialogs(userId, page, pageSize));
    }

    /**
     * Создаёт личный диалог между двумя пользователями.
     *
     * @param firstUserId  идентификатор первого участника
     * @param secondUserId идентификатор второго участника
     * @return результат с данными созданного или существующего диалога
     */
    @Override
    public CreateDialogResult createDialog(Long firstUserId, Long secondUserId) {
        return new CreateDialogResult(
                messageService.createDialog(firstUserId, secondUserId)
        );
    }

    /**
     * Возвращает или создаёт диалог поддержки между пользователем и администратором.
     *
     * @param authUserId идентификатор текущего пользователя
     * @return результат с данными диалога поддержки
     */
    @Override
    public CreateDialogResult getSupportDialog(Long authUserId) {
        return new CreateDialogResult(
                messageService.createDialog(ADMIN_ID, authUserId)
        );
    }

    /**
     * Помечает входящие сообщения диалога доставленными и уведомляет отправителей.
     *
     * @param userId   идентификатор текущего пользователя
     * @param dialogId идентификатор диалога
     */
    @Override
    @Transactional
    public void receiveMessages(Long userId, Long dialogId) {
        Dialog dialog = messageService.findDialogById(dialogId);
        List<Message> justNowReceivedMessages = messageService.receiveMessages(dialog.getMessages(), userId);

        if (!justNowReceivedMessages.isEmpty()) {
            ChangeMessageStatusEvent event = new ChangeMessageStatusEvent()
                    .setDialog(dialog)
                    .setStatus(READ);

            eventService.send(DtoMapper.map(event), event.getUsersToSendNotification(), CHANGE_STATUS);
        }
    }

    /**
     * Помечает сообщения диалога прочитанными и уведомляет других участников.
     *
     * @param userId   идентификатор текущего пользователя
     * @param dialogId идентификатор диалога
     */
    @Override
    @Transactional
    public void readMessages(Long userId, Long dialogId) {
        Dialog dialog = messageService.findDialogById(dialogId);
        if (!messageService.readMessages(dialog.getMessages(), userId).isEmpty()) {
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

    /**
     * Возвращает вложения диалога.
     *
     * @param id идентификатор диалога
     * @return результат со списком вложений
     */
    @Override
    public GetDialogAttachmentsResult getAttachments(Long dialogId) {
        return new GetDialogAttachmentsResult(
                messageService.getAttachments(dialogId)
        );
    }

    /**
     * Сохраняет файл вложения во внешнем хранилище.
     *
     * @param inputStream      поток данных файла
     * @param originalFilename исходное имя файла
     * @param contentType      MIME-тип содержимого
     * @return результат с URI сохранённого файла
     */
    @Override
    public UploadAttachmentResult store(InputStream inputStream, String originalFilename, String contentType) {
        return new UploadAttachmentResult()
                .setUri(fileStorageService.store(inputStream, originalFilename, contentType));
    }
}
