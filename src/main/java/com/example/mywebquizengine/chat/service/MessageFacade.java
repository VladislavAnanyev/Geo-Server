package com.example.mywebquizengine.chat.service;

import com.example.mywebquizengine.chat.model.dto.output.DialogView;
import com.example.mywebquizengine.chat.model.dto.output.LastDialog;
import com.example.mywebquizengine.chat.model.CreateGroupModel;
import com.example.mywebquizengine.chat.model.SendMessageModel;
import org.springframework.stereotype.Component;

import java.util.List;

public interface MessageFacade {
    void sendMessage(SendMessageModel sendMessageModel);

    void typingMessage(Long dialogId, Long userId);

    void deleteMessage(Long messageId, Long userId);

    void editMessage(Long messageId, String content, Long userId);

    DialogView getChatRoom(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId);

    List<LastDialog> getLastDialogs(Long userId);

    Long createGroup(CreateGroupModel model);

    Long createDialog(Long firstUserId, Long secondUserId);
}
