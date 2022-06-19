package com.example.mywebquizengine.model.chat.dto.output;

import com.example.mywebquizengine.model.chat.domain.MessagePhoto;
import com.example.mywebquizengine.model.chat.domain.MessageStatus;
import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;
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
    List<MessagePhoto> getPhotos();
    @Value("#{target.dialog.dialogId}")
    Long getDialogId();
    String getUniqueCode();
    MessageStatus getStatus();
}
