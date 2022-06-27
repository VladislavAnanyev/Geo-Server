package com.example.mywebquizengine.request.model.dto.output;

import com.example.mywebquizengine.chat.model.dto.output.MessageView;
import com.example.mywebquizengine.meeting.model.dto.output.MeetingCommonView;
import com.example.mywebquizengine.user.model.dto.UserCommonView;

public interface RequestView {
    Long getRequestId();
    UserCommonView getSender();
    UserCommonView getTo();
    String getStatus();
    MeetingCommonView getMeeting();
    MessageView getMessage();
}
