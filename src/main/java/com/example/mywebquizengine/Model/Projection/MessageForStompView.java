package com.example.mywebquizengine.Model.Projection;

import java.util.Calendar;

public interface MessageForStompView {
    Integer getId();
    String getContent();
    UserForMessageView getSender();
    Calendar getTimestamp();
    DialogForStomp getDialog();
    //Long getDialogId();
}
