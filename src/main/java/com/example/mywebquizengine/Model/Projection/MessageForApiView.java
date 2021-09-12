package com.example.mywebquizengine.Model.Projection;

import java.util.Calendar;

public interface MessageForApiView {
    Integer getId();
    String getContent();
    UserView getSender();
    Calendar getTimestamp();
    DialogForApi getDialog();
    //Long getDialogId();
}
