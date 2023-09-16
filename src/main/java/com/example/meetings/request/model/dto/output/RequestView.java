package com.example.meetings.request.model.dto.output;

import com.example.meetings.chat.model.dto.output.MessageView;
import com.example.meetings.meeting.model.dto.output.MeetingCommonView;
import com.example.meetings.user.model.dto.UserCommonView;

public interface RequestView {
    Long getRequestId();
    UserCommonView getSender();
    UserCommonView getTo();
    String getStatus();
    MeetingCommonView getMeeting();
    MessageView getMessage();
}
