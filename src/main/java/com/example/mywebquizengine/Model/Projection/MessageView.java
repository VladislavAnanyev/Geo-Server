package com.example.mywebquizengine.Model.Projection;

import java.util.Calendar;

public interface MessageView {
    String getContent();
    UserForMessageView getSender();
    Calendar getTimestamp();
    //DialogWithUsersView getDialog();
}
