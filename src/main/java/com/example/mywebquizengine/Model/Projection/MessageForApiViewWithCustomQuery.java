package com.example.mywebquizengine.Model.Projection;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.util.Calendar;

public interface MessageForApiViewWithCustomQuery {

    Integer getId();

    String getContent();

    @Value("#{new com.example.mywebquizengine.Model.User(target.username, target.firstName, target.lastName, target.avatar)}")
    UserForMessageView getSender();

    Timestamp getTimestamp();

    @Value("#{new com.example.mywebquizengine.Model.Chat.Dialog(target.dialogId, target.name, target.image)}")
    DialogForApi getDialog();
    //Long getDialogId();
}
