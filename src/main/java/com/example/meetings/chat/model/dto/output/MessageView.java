package com.example.meetings.chat.model.dto.output;

import com.example.meetings.chat.model.domain.MessageFile;
import com.example.meetings.chat.model.domain.MessageStatus;
import com.example.meetings.user.model.dto.UserCommonView;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;

public interface MessageView {
    Long getMessageId();

    String getContent();

    UserCommonView getSender();

    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    Date getTimestamp();

    List<MessageView> getForwardedMessages();

    List<MessageFile> getFiles();

    @Value("#{target.dialog.dialogId}")
    Long getDialogId();

    String getUniqueCode();

    MessageStatus getStatus();
}
