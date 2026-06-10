package com.example.meetings.chat.service;


import com.example.meetings.chat.mapper.DtoMapper;
import com.example.meetings.chat.model.*;
import com.example.meetings.chat.model.domain.*;
import com.example.meetings.chat.model.dto.input.Typing;
import com.example.meetings.chat.model.dto.output.LastDialog;
import com.example.meetings.chat.repository.DialogRepository;
import com.example.meetings.chat.repository.MessageHistoryRepository;
import com.example.meetings.chat.repository.MessageRepository;
import com.example.meetings.common.exception.LogicException;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.util.Constants;
import com.example.meetings.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.meetings.chat.mapper.DialogDTOMapper.getImage;
import static com.example.meetings.chat.mapper.DialogDTOMapper.getName;
import static com.example.meetings.chat.mapper.DtoMapper.map;
import static com.example.meetings.chat.model.domain.MessageStatus.DELIVERED;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Бизнес-логика чата: диалоги, сообщения, статусы доставки и групповые беседы.
 */
@Service
@Validated
public class MessageService {

    private final MessageRepository messageRepository;
    private final DialogRepository dialogRepository;
    private final UserService userService;
    private final MessageHistoryRepository messageHistoryRepository;
    @Value("${hostname}")
    private String hostname;

    public MessageService(MessageRepository messageRepository, DialogRepository dialogRepository,
                          UserService userService, MessageHistoryRepository messageHistoryRepository) {
        this.messageRepository = messageRepository;
        this.dialogRepository = dialogRepository;
        this.userService = userService;
        this.messageHistoryRepository = messageHistoryRepository;
    }

    /**
     * Создаёт личный диалог между двумя пользователями или возвращает существующий.
     *
     * @param firstUserId  идентификатор первого участника
     * @param secondUserId идентификатор второго участника
     * @return идентификатор диалога
     */
    public Long createDialog(Long firstUserId, Long secondUserId) {
        if (secondUserId.equals(firstUserId)) {
            throw new LogicException("exception.dialog.create.self");
        }

        Long dialogId = dialogRepository.findDialogBetweenUsers(firstUserId, secondUserId);
        if (dialogId != null) {
            return dialogId;
        }

        Dialog dialog = new Dialog();
        dialog.addUser(userService.loadUserByUserId(firstUserId));
        dialog.addUser(userService.loadUserByUserId(secondUserId));
        dialog.setType(DialogType.PRIVATE);
        dialogRepository.save(dialog);

        return dialog.getDialogId();
    }

    /**
     * Возвращает список диалогов пользователя с последним сообщением и счётчиком непрочитанных.
     *
     * @param userId идентификатор пользователя
     * @return список диалогов для отображения в списке чатов
     */
    public List<LastDialogDTO> getDialogs(Long userId) {
        List<LastDialog> dialogs = messageRepository.getLastDialogs(userId);

        return mapToLastDialogsDTO(userId, dialogs);
    }

    /**
     * Помечает сообщение удалённым от имени отправителя.
     *
     * @param messageId идентификатор сообщения
     * @param userId    идентификатор текущего пользователя
     * @return обновлённое сообщение
     */
    @Transactional
    public Message setDeletedStatus(Long messageId, Long userId) {
        Message message = findMessageById(messageId);
        if (!isCanModifyMessage(message.getSender().getUserId(), userId)) {
            throw new SecurityException("exception.message.delete.forbidden");
        }

        message.setStatus(MessageStatus.DELETED);
        updateMessageStatusHistory(userId, message, MessageStatus.DELETED);
        return message;
    }

    /**
     * Загружает диалог с постраничным списком сообщений для участника.
     *
     * @param dialogId идентификатор диалога
     * @param page     номер страницы
     * @param pageSize размер страницы
     * @param sortBy   поле сортировки сообщений
     * @param userId   идентификатор текущего пользователя
     * @return диалог с сообщениями
     */
    @Transactional
    public Dialog getDialog(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId) {
        Dialog dialog = findDialogById(dialogId);

        if (!isUserContainsInDialog(dialog, userId)) {
            throw new SecurityException("exception.dialog.not.member");
        }

        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        List<Message> messages = messageRepository.findAllByDialog_DialogIdAndStatusNot(
                dialogId,
                MessageStatus.DELETED,
                paging
        ).getContent();

        List<Message> messageList = new ArrayList<>(messages);
        Collections.reverse(messageList);
        dialog.setMessages(messageList);

        return dialog;
    }

    /**
     * Редактирует текст сообщения отправителем.
     *
     * @param messageId  идентификатор сообщения
     * @param content    новый текст
     * @param authUserId идентификатор текущего пользователя
     * @return обновлённое сообщение
     */
    @Transactional
    public Message editMessage(Long messageId, String content, Long authUserId) {
        Message message = findMessageById(messageId);
        if (!isCanModifyMessage(message.getSender().getUserId(), authUserId)) {
            throw new SecurityException("exception.message.edit.forbidden");
        }

        message.setStatus(MessageStatus.EDITED);
        message.setContent(content);
        updateMessageStatusHistory(authUserId, message, MessageStatus.EDITED);
        return message;
    }

    /**
     * Сохраняет новое сообщение в диалоге и обновляет последнее сообщение диалога.
     *
     * @param request данные отправляемого сообщения
     * @return сохранённое сообщение или {@code null}, если сообщение с таким uniqueCode уже есть
     */
    @Transactional
    public Message saveMessage(@Valid SendMessageModel request) {
        if (messageRepository.existsBySender_UserIdAndUniqueCode(request.getSenderId(), request.getUniqueCode())) {
            return null;
        }

        Dialog dialog = findDialogById(request.getDialogId());
        if (!isUserContainsInDialog(dialog, request.getSenderId())) {
            throw new SecurityException("exception.dialog.not.member");
        }
        Message message = create(request, dialog);
        message.setStatus(DELIVERED);
        updateMessageStatusHistory(request.getSenderId(), message, DELIVERED);
        dialog.setLastMessage(message);

        return messageRepository.save(message);
    }

    /**
     * Создаёт групповой диалог с указанными участниками.
     *
     * @param model параметры группы: участники, название и изображение
     * @return сохранённый групповой диалог
     */
    public Dialog createGroup(CreateGroupModel model) {
        Dialog dialog = new Dialog();
        dialog.setType(DialogType.GROUP);
        dialog.setImage(getUrl(model));
        model.getUsers().forEach(userId -> dialog.addUser(userService.loadUserByUserId(userId)));

        if (!isUserContainsInDialog(dialog, model.getAuthUserId())) {
            throw new SecurityException("exception.dialog.not.member");
        }

        if (model.getName() == null || model.getName().isEmpty()) {
            dialog.setName("Конференция");
        } else {
            dialog.setName(model.getName());
        }

        return dialogRepository.save(dialog);
    }

    /**
     * Добавляет пользователя в групповой диалог.
     *
     * @param userId   идентификатор добавляемого пользователя
     * @param dialogId идентификатор группового диалога
     */
    @Transactional
    public void addUserToGroupDialog(Long userId, Long dialogId) {
        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow();
        dialog.addUser(userService.loadUserByUserId(userId));
    }

    /**
     * Удаляет пользователя из группового диалога.
     *
     * @param userId   идентификатор удаляемого пользователя
     * @param dialogId идентификатор группового диалога
     */
    @Transactional
    public void deleteUserFromGroupDialog(Long userId, Long dialogId) {
        Dialog dialog = dialogRepository.findById(dialogId).orElseThrow();
        dialog.removeUser(userService.loadUserByUserId(userId));
    }

    /**
     * Скрывает диалог из списка чатов пользователя.
     *
     * @param dialogId идентификатор диалога
     */
    @Transactional
    public void hideDialog(Long dialogId) {
        Dialog dialog = findDialogById(dialogId);
        dialog.setVisible(false);
        dialogRepository.save(dialog);
    }

    /**
     * Формирует событие «пользователь печатает» для диалога.
     *
     * @param dialogId идентификатор диалога
     * @param userId   идентификатор текущего пользователя
     * @return данные события набора текста
     */
    @Transactional
    public Typing typingMessage(Long dialogId, Long userId) {
        Dialog dialog = findDialogById(dialogId);
        User authUser = userService.loadUserByUserId(userId);

        if (!isUserContainsInDialog(dialog, userId)) {
            throw new SecurityException("exception.dialog.not.member");
        }

        Typing typing = new Typing();
        typing.setUser(authUser);
        typing.setDialog(dialog);
        return typing;
    }

    /**
     * Помечает сообщения как прочитанные для указанного пользователя.
     *
     * @param messages список сообщений
     * @param userId   идентификатор читателя
     * @return сообщения, впервые получившие статус {@code READ}
     */
    @Transactional
    public List<Message> readMessages(List<Message> messages, Long userId) {
        return addMessagesStatus(
                messages,
                userId,
                MessageStatus.READ
        );
    }

    /**
     * Помечает сообщения как доставленные получателю.
     *
     * @param messages список сообщений
     * @param userId   идентификатор получателя
     * @return сообщения, впервые получившие статус {@code RECEIVED}
     */
    @Transactional
    public List<Message> receiveMessages(List<Message> messages, Long userId) {
        return addMessagesStatus(
                messages,
                userId,
                MessageStatus.RECEIVED
        );
    }

    /**
     * Находит сообщение в конкретном диалоге.
     *
     * @param dialogId  идентификатор диалога
     * @param messageId идентификатор сообщения
     * @return найденное сообщение
     */
    public Message findMessageByDialogIdAndMessageId(Long dialogId, Long messageId) {
        return messageRepository.findMessageByDialogDialogIdAndMessageId(dialogId, messageId)
                .orElseThrow(() -> new EntityNotFoundException("exception.message.not.found"));
    }

    /**
     * Загружает диалог по идентификатору.
     *
     * @param dialogId идентификатор диалога
     * @return найденный диалог
     */
    public Dialog findDialogById(Long dialogId) {
        Optional<Dialog> dialog = dialogRepository.findById(dialogId);
        if (dialog.isPresent()) {
            return dialog.get();
        } else {
            throw new EntityNotFoundException("exception.dialog.not.found");
        }
    }

    /**
     * Возвращает вложения всех сообщений диалога.
     *
     * @param dialogId идентификатор диалога
     * @return список файловых вложений
     */
    public List<FileResponse> getAttachments(Long dialogId) {
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialogId);
        if (optionalDialog.isEmpty()) {
            throw new EntityNotFoundException("exception.dialog.not.found");
        }

        Dialog dialog = optionalDialog.get();
        List<FileResponse> files = new ArrayList<>();
        for (Message message : dialog.getMessages()) {
            for (MessageFile file : message.getFiles()) {
                FileResponse fileResponse = new FileResponse()
                        .setFilename(file.getFilename())
                        .setContentType(file.getContentType())
                        .setOriginalName(file.getOriginalName());
                files.add(fileResponse);
            }
        }
        return files;
    }

    private List<Message> addMessagesStatus(List<Message> messages, Long userId, MessageStatus messageStatus) {
        List<Message> justNowReadMessages = new ArrayList<>();
        for (Message message : messages) {
            boolean isAlreadyRead = isStatusInfoExistInHistory(
                    message,
                    userId,
                    messageStatus
            );
            if (!isAlreadyRead) {
                updateMessageStatusHistory(userId, message, messageStatus);
                justNowReadMessages.add(message);
            }
        }
        return justNowReadMessages;
    }

    private boolean isStatusInfoExistInHistory(Message message, Long userId, MessageStatus messageStatus) {
        List<MessageStatusHistory> historyList = message.getMessageStatusHistoryList();

        return historyList.stream().anyMatch(
                historyItem -> historyItem.getUser().getUserId().equals(userId) &&
                               historyItem.getMessageStatus().equals(messageStatus)
        );
    }

    private void updateMessageStatusHistory(Long authUserId, Message message, MessageStatus messageStatus) {
        messageHistoryRepository.save(
                new MessageStatusHistory()
                        .setUser(userService.loadUserByUserIdProxy(authUserId))
                        .setTimestamp(new Date())
                        .setMessage(message)
                        .setMessageStatus(messageStatus)
        );
    }

    private Message findMessageById(Long messageId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent()) {
            return message.get();
        } else {
            throw new EntityNotFoundException("exception.message.not.found");
        }
    }

    private boolean isCanModifyMessage(Long senderId, Long userId) {
        return senderId.equals(userId);
    }

    private boolean isUserContainsInDialog(Dialog dialog, Long userId) {
        return dialog.getUsers().stream().anyMatch(user -> user.getUserId().equals(userId));
    }

    private List<LastDialogDTO> mapToLastDialogsDTO(Long userId, List<LastDialog> dialogs) {
        List<LastDialogDTO> lastDialogs = new ArrayList<>();
        for (LastDialog lastDialog : dialogs) {
            Dialog dialog = findDialogById(lastDialog.getDialogId());
            lastDialogs.add(
                    new LastDialogDTO()
                            .setDialogId(lastDialog.getDialogId())
                            .setContent(lastDialog.getContent())
                            .setTimestamp(lastDialog.getTimestamp())
                            .setLastSender(map(userService.loadUserByUserId(lastDialog.getUserId())))
                            .setUsers(
                                    dialog.getUsers()
                                            .stream()
                                            .map(DtoMapper::map)
                                            .collect(toSet())
                            )
                            .setUnreadMessages(
                                    messageHistoryRepository.findCountOfUnreadMessagesInDialogByUser(
                                            dialog.getDialogId(),
                                            userId
                                    )
                            )
                            .setType(dialog.getType().toString())
                            .setName(getName(dialog, userId))
                            .setImage(getImage(dialog, userId))
            );
        }

        return lastDialogs;
    }

    /**
     * Собирает сущность сообщения без сохранения в базу.
     *
     * @param request данные отправляемого сообщения
     * @param dialog  диалог назначения
     * @return подготовленное сообщение
     */
    public Message create(SendMessageModel request, Dialog dialog) {
        User sender = userService.loadUserByUserIdProxy(request.getSenderId());
        Message message = new Message();
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setTimestamp(new Date());
        message.setUniqueCode(request.getUniqueCode() == null ? UUID.randomUUID().toString() : request.getUniqueCode());
        message.setDialog(dialog);
        if (request.getForwardedMessages() != null) {
            List<Message> processedForwardedMessages = processForwardedMessages(
                    request.getForwardedMessages(),
                    request.getSenderId()
            );
            message.setForwardedMessages(processedForwardedMessages);
        }

        if (request.getFiles() != null) {
            List<MessageFile> messageFiles = request.getFiles().stream().map(file -> new MessageFile()
                    .setFilename(file.getFilename())
                    .setUri(file.getUri())
                    .setContentType(file.getContentType())
                    .setOriginalName(file.getOriginalName())).collect(toList());

            message.setFiles(messageFiles);
        }

        return message;
    }

    private List<Message> processForwardedMessages(ForwardedMessages messages, Long userId) {
        Dialog dialog = findDialogById(messages.getDialogId());
        if (dialog.getUsers().stream().noneMatch(user -> user.getUserId().equals(userId))) {
            throw new ResponseStatusException(
                    FORBIDDEN,
                    "exception.forwarded.dialog.not.member"
            );
        }

        List<Message> forwardedMessages = new ArrayList<>();
        for (Long messageId : messages.getMessagesId()) {
            forwardedMessages.add(
                    findMessageByDialogIdAndMessageId(
                            dialog.getDialogId(),
                            messageId
                    )
            );
        }

        return forwardedMessages;
    }

    private String getUrl(CreateGroupModel model) {
        if (model.getImage() != null) {
            return model.getImage();
        }

        int imageIndex = ThreadLocalRandom.current().nextInt(1, 6);
        return String.format(
                Constants.PLAN_DEFAULT_IMAGE_URL_TEMPLATE,
                imageIndex);
    }
}
