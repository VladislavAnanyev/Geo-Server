package com.example.mywebquizengine.chat.facade;

import com.example.mywebquizengine.chat.model.CreateGroupModel;
import com.example.mywebquizengine.chat.model.FileResponse;
import com.example.mywebquizengine.chat.model.SendMessageModel;
import com.example.mywebquizengine.chat.model.dto.output.DialogView;
import com.example.mywebquizengine.chat.model.dto.output.LastDialog;

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

    void receiveMessages(Long userId, Long dialogId);

    List<FileResponse> getAttachments(Long id);
}
