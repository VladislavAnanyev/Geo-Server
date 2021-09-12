package com.example.mywebquizengine.Model.Projection;

import java.util.Calendar;

public interface MessageView {
    String getContent();
    UserView getSender();
    Calendar getTimestamp();
    //DialogWithUsersView getDialog();
}
