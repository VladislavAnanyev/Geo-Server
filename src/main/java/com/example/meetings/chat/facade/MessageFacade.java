package com.example.meetings.chat.facade;

import com.example.meetings.chat.model.*;
import com.example.meetings.chat.model.dto.output.*;

import java.io.InputStream;

public interface MessageFacade {
    void sendMessage(SendMessageModel sendMessageModel);

    void typingMessage(Long dialogId, Long userId);

    void deleteMessage(Long messageId, Long userId);

    void editMessage(Long messageId, String content, Long userId);

    DialogDTO getChatRoom(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId);

    GetDialogsResult getLastDialogs(Long userId);

    CreateDialogResult createDialog(Long firstUserId, Long secondUserId);

    void receiveMessages(Long userId, Long dialogId);

    void readMessages(Long userId, Long dialogId);

    GetDialogAttachmentsResult getAttachments(Long id);

    UploadAttachmentResult store(InputStream inputStream, String originalFilename, String contentType);
}
