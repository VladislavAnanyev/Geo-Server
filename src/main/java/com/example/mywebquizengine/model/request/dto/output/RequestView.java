package com.example.mywebquizengine.model.request.dto.output;

import com.example.mywebquizengine.model.chat.dto.output.MessageView;
import com.example.mywebquizengine.model.geo.dto.output.MeetingCommonView;
import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;

public interface RequestView {
    Long getRequestId();
    UserCommonView getSender();
    UserCommonView getTo();
    String getStatus();
    MeetingCommonView getMeeting();
    MessageView getMessage();
}
