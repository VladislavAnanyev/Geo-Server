package com.example.mywebquizengine.model.projection;

public interface ReceivedRequestView {

    Long getId();

    UserCommonView getSender();

    //UserView getTo();

    String getStatus();

    MeetingCommonView getMeeting();

    MessageView getMessage();

    /*@Value("REQUEST")
    String getType();*/
}
