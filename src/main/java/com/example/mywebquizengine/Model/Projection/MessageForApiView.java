package com.example.mywebquizengine.Model.Projection;

import java.util.Calendar;

public interface MessageForApiView {
    Integer getId();
    String getContent();
    UserCommonView getSender();
    Calendar getTimestamp();
    DialogForApi getDialog();
    //Long getDialogId();
}

/*public interface MessageForApiView {
    Integer getId();
    String getContent();
    @Value("#{new com.example.mywebquizengine.Model.User(target.username, target.firstName, target.lastName, target.avatar)}")
    UserForMessageView getSender();

    Calendar getTimestamp();

    @Value("#{new com.example.mywebquizengine.Model.Chat.Dialog(target.dialogId, target.name, target.image)}")
    DialogForApi getDialog();
    //Long getDialogId();
}*/
