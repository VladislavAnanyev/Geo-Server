package com.example.meetings.chat.service;


import com.example.meetings.chat.mapper.DtoMapper;
import com.example.meetings.chat.model.*;
import com.example.meetings.chat.model.domain.*;
import com.example.meetings.chat.model.dto.input.Typing;
import com.example.meetings.chat.model.dto.output.LastDialog;
import com.example.meetings.chat.repository.*;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

import static com.example.meetings.chat.mapper.DialogDTOMapper.getImage;
import static com.example.meetings.chat.mapper.DialogDTOMapper.getName;
import static com.example.meetings.chat.mapper.DtoMapper.map;
import static com.example.meetings.chat.model.domain.MessageStatus.DELIVERED;
import static java.util.stream.Collectors.toSet;

@Service
@Validated
public class MessageService {

    private final MessageRepository messageRepository;
    private final DialogRepository dialogRepository;
    private final UserService userService;
    private final MessageFactory messageFactory;
    private final MessageHistoryRepository messageHistoryRepository;
    @Value("${hostname}")
    private String hostname;

    public MessageService(MessageRepository messageRepository, DialogRepository dialogRepository,
                          UserService userService, MessageFactory messageFactory, MessageHistoryRepository messageHistoryRepository) {
        this.messageRepository = messageRepository;
        this.dialogRepository = dialogRepository;
        this.userService = userService;
        this.messageFactory = messageFactory;
        this.messageHistoryRepository = messageHistoryRepository;
    }

    public Long createDialog(Long firstUserId, Long secondUserId) {
        if (secondUserId.equals(firstUserId)) {
            throw new IllegalArgumentException("You can not create dialog with yourself");
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

    public List<LastDialogDTO> getDialogs(Long userId) {
        List<LastDialog> dialogs = messageRepository.getLastDialogs(userId);

        return mapToLastDialogsDTO(userId, dialogs);
    }

    @Transactional
    public Message setDeletedStatus(Long messageId, Long userId) {
        Message message = findMessageById(messageId);
        if (!isCanModifyMessage(message.getSender().getUserId(), userId)) {
            throw new SecurityException("Вы не можете удалить это сообщение");
        }

        message.setStatus(MessageStatus.DELETED);
        updateMessageStatusHistory(userId, message, MessageStatus.DELETED);
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

        message.setStatus(MessageStatus.EDITED);
        message.setContent(content);
        updateMessageStatusHistory(authUserId, message, MessageStatus.EDITED);
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
        message.setStatus(DELIVERED);
        updateMessageStatusHistory(sendMessageModel.getSenderId(), message, DELIVERED);
        dialog.setLastMessage(message);
        return messageRepository.save(message);
    }

    public Dialog createGroup(CreateGroupModel model) {
        Dialog dialog = new Dialog();
        dialog.setType(DialogType.GROUP);
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

    public List<Message> readMessages(List<Message> messages, Long userId) {
        return tryToChangeMessagesStatus(
                messages,
                userId,
                MessageStatus.READ
        );
    }

    public List<Message> receiveMessages(List<Message> messages, Long userId) {
        return tryToChangeMessagesStatus(
                messages,
                userId,
                MessageStatus.RECEIVED
        );
    }

    public Dialog findDialogById(Long dialogId) {
        Optional<Dialog> dialog = dialogRepository.findById(dialogId);
        if (dialog.isPresent()) {
            return dialog.get();
        } else {
            throw new EntityNotFoundException("Dialog not found");
        }
    }

    public List<FileResponse> getAttachments(Long dialogId) {
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialogId);
        if (optionalDialog.isEmpty()) {
            throw new EntityNotFoundException("dialog not found");
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

    private List<Message> tryToChangeMessagesStatus(List<Message> messages, Long userId, MessageStatus messageStatus) {
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
        MessageStatusHistory statusHistoryInfo = new MessageStatusHistory()
                .setUser(userService.loadUserByUserIdProxy(authUserId))
                .setTimestamp(new Date())
                .setMessage(message)
                .setMessageStatus(messageStatus);

        if (message.getMessageStatusHistoryList() == null) {
            message.setMessageStatusHistoryList(
                    Collections.singletonList(statusHistoryInfo)
            );
        } else {
            message.getMessageStatusHistoryList().add(
                    statusHistoryInfo
            );
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
}
