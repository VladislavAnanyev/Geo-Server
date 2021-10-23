package com.example.mywebquizengine.Model.Projection;

import com.example.mywebquizengine.Model.Chat.Message;

public interface ReceivedRequestView {

    Long getId();

    UserCommonView getSender();

    //UserView getTo();

    String getStatus();

    MeetingCommonView getMeeting();

    MessageView getMessage();
}
