package com.example.mywebquizengine.model.projection;

import com.example.mywebquizengine.model.chat.MessagePhoto;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;

public interface MessageView {
    Long getId();
    String getContent();
    UserCommonView getSender();
    Date getTimestamp();
    List<MessageView> getForwardedMessages();
    List<MessagePhoto> getPhotos();
    @Value("#{target.dialog.dialogId}")
    Long getDialogId();
    String getUniqueCode();
    /*String getType();*/
}
