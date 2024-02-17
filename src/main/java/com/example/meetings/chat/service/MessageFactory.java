package com.example.meetings.chat.service;

import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.chat.model.domain.Message;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class MessageFactory {

    @Autowired
    private UserService userService;

    public Message create(String content, String uniqueCode, Long senderId, Dialog dialog) {
        User sender = userService.loadUserByUserIdProxy(senderId);
        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(new Date());
        message.setUniqueCode(uniqueCode == null ? UUID.randomUUID().toString() : uniqueCode);
        message.setDialog(dialog);
        return message;
    }
}
