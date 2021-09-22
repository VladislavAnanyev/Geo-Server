package com.example.mywebquizengine.Model.Projection;

import java.util.Calendar;
import java.util.GregorianCalendar;

public interface MessageForApiView {
    Integer getId();
    String getContent();
    UserForMessageView getSender();
    Calendar getTimestamp();
    DialogForApi getDialog();
    //Long getDialogId();
}
