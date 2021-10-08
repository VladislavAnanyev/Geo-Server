package com.example.mywebquizengine.Model.Projection;

import com.example.mywebquizengine.Model.Chat.Message;

public interface RequestView {

    Long getId();

    UserView getSender();

    UserView getTo();

    String getStatus();

    MeetingCommonView getMeeting();

    Message getMessage();
}
