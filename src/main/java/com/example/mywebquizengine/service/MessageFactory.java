package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.chat.domain.Dialog;
import com.example.mywebquizengine.model.chat.domain.Message;
import com.example.mywebquizengine.model.chat.domain.MessageStatus;
import com.example.mywebquizengine.model.userinfo.domain.User;
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
        message.setStatus(MessageStatus.DELIVERED);
        message.setUniqueCode(uniqueCode == null ? UUID.randomUUID().toString() : uniqueCode);
        message.setDialog(dialog);
        return message;
    }
}
