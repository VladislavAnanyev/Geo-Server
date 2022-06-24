package com.example.mywebquizengine.service;

import com.example.mywebquizengine.service.model.SendMessageModel;
import org.springframework.stereotype.Component;

@Component
public interface MessageFacade {
    void sendMessage(SendMessageModel sendMessageModel, Long userId);

    void typingMessage(Long dialogId, Long userId);
}
