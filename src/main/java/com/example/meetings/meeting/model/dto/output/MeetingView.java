package com.example.meetings.meeting.model.dto.output;

import com.example.meetings.user.model.dto.UserCommonView;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface MeetingView {
    Long getMeetingId();

    @Value("#{T(com.example.meetings.common.utils.UserUtil).getUserForMeeting(target.firstUserId, target.secondUserId)}")
    UserCommonView getUser();

    Double getLng();

    Double getLat();

    Date getTime();

    @Value("#{@requestService.isPossibleToSendRequest(target.meetingId)}")
    boolean isPossibleToSendRequest();
}
