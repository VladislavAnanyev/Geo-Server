package com.example.mywebquizengine.Model.Projection;

public interface RequestView {

    Long getId();

    UserView getSender();

    UserView getTo();

    String getStatus();

    MeetingCommonView getMeeting();

    String getMessage();
}
