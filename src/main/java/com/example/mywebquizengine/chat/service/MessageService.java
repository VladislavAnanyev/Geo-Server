package com.example.mywebquizengine.chat.service;


import com.example.mywebquizengine.chat.model.CreateGroupModel;
import com.example.mywebquizengine.chat.model.FileResponse;
import com.example.mywebquizengine.chat.model.SendMessageModel;
import com.example.mywebquizengine.chat.model.domain.*;
import com.example.mywebquizengine.chat.model.dto.input.Typing;
import com.example.mywebquizengine.chat.model.dto.output.LastDialog;
import com.example.mywebquizengine.chat.repository.DialogRepository;
import com.example.mywebquizengine.chat.repository.MessageRepository;
import com.example.mywebquizengine.common.utils.ProjectionUtil;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.model.dto.UserCommonView;
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
import java.util.*;

@Service
@Validated
public class MessageService {

    private final MessageRepository messageRepository;
    private final DialogRepository dialogRepository;
    private final UserService userService;
    private final MessageFactory messageFactory;
    @Value("${hostname}")
    private String hostname;
    private final ProjectionUtil projectionUtil;

    public MessageService(MessageRepository messageRepository, DialogRepository dialogRepository,
                          UserService userService, MessageFactory messageFactory, ProjectionUtil projectionUtil) {
        this.messageRepository = messageRepository;
        this.dialogRepository = dialogRepository;
        this.userService = userService;
        this.messageFactory = messageFactory;
        this.projectionUtil = projectionUtil;
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
            dialog.setType(DialogType.PRIVATE);
            dialogRepository.save(dialog);
            return dialog.getDialogId();
        }
    }

    public List<LastDialog> getDialogs(Long userId) {
        return messageRepository.getLastDialogs(userId);
    }

    @Transactional
    public List<NewLastDialog> getDialogsV2(Long userId) {
        User user = userService.loadUserByUserId(userId);
        Set<Dialog> dialogs = user.getDialogs();

        List<NewLastDialog> lastDialogs = new ArrayList<>();

        for (Dialog dialog : dialogs) {
            if (dialog.getLastMessage() != null) {
                NewLastDialog newLastDialog = new NewLastDialog();
                newLastDialog.setDialogId(dialog.getDialogId());
                newLastDialog.setContent(dialog.getLastMessage().getContent());
                newLastDialog.setTimestamp(dialog.getLastMessage().getTimestamp());

                if (dialog.getName() == null) {
                    Set<User> userSet = new HashSet<>(dialog.getUsers());
                    userSet.removeIf(user2 -> user2.getUserId().equals(userId));
                    newLastDialog.setName(userSet.iterator().next().getUsername());
                    newLastDialog.setImage(userSet.iterator().next().getAvatar());
                } else {
                    newLastDialog.setName(dialog.getName());
                    newLastDialog.setImage(dialog.getImage());
                }

                newLastDialog.setStatus(dialog.getLastMessage().getStatus().toString());
                newLastDialog.setLastSender(projectionUtil.parse(dialog.getLastMessage().getSender(), UserCommonView.class));
                lastDialogs.add(newLastDialog);
            }
        }

        return lastDialogs;
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
        message.setStatus(MessageStatus.DELIVERED);
        updateMessageStatusHistory(sendMessageModel.getSenderId(), message, MessageStatus.DELIVERED);
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
}
