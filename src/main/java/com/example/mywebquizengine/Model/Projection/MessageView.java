package com.example.mywebquizengine.Model.Projection;

import java.time.ZonedDateTime;

public interface MessageView {
    Integer getId();
    String getContent();
    UserCommonView getSender();
    ZonedDateTime getTimestamp();
    //DialogWithUsersView getDialog();
}
