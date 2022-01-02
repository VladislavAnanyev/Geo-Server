package com.example.mywebquizengine.model.projection;

public interface RequestView {
    Long getId();
    UserCommonView getSender();
    UserCommonView getTo();
    String getStatus();
    MeetingCommonView getMeeting();
    MessageView getMessage();
}
