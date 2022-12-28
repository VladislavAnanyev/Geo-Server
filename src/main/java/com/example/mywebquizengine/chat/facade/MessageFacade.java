package com.example.mywebquizengine.chat.facade;

import com.example.mywebquizengine.chat.model.SendMessageModel;
import com.example.mywebquizengine.chat.model.dto.output.CreateDialogResult;
import com.example.mywebquizengine.chat.model.dto.output.DialogView;
import com.example.mywebquizengine.chat.model.dto.output.GetDialogAttachmentsResult;
import com.example.mywebquizengine.chat.model.dto.output.GetDialogsResult;

import java.io.InputStream;

public interface MessageFacade {
    void sendMessage(SendMessageModel sendMessageModel);

    void typingMessage(Long dialogId, Long userId);

    void deleteMessage(Long messageId, Long userId);

    void editMessage(Long messageId, String content, Long userId);

    DialogView getChatRoom(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId);

    GetDialogsResult getLastDialogs(Long userId);

    CreateDialogResult createDialog(Long firstUserId, Long secondUserId);

    void receiveMessages(Long userId, Long dialogId);

    GetDialogAttachmentsResult getAttachments(Long id);

    UploadAttachmentResult store(InputStream inputStream, String originalFilename, String contentType);
}
