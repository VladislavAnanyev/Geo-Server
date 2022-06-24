package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.chat.domain.Message;
import com.example.mywebquizengine.model.chat.dto.input.Typing;
import com.example.mywebquizengine.model.chat.dto.output.MessageView;
import com.example.mywebquizengine.model.chat.dto.output.TypingView;
import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.chat.MessageService;
import com.example.mywebquizengine.service.model.SendMessageModel;
import com.example.mywebquizengine.service.sender.NotificationService;
import com.example.mywebquizengine.service.utils.ProjectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Component
public class MessageFacadeImpl implements MessageFacade {

    @Autowired
    private MessageService messageService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ProjectionUtil projectionUtil;

    @Override
    public void sendMessage(SendMessageModel sendMessageModel, Long userId) {
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
}
