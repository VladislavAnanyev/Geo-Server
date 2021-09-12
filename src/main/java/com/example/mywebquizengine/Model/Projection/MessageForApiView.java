package com.example.mywebquizengine.Model.Projection;

public interface MessageForApiView {
    Integer getId();
    String getContent();
    UserView getSender();
    //Calendar getTimestamp();
    DialogForApi getDialog();
    //Long getDialogId();
}
