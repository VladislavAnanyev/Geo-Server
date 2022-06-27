package com.example.mywebquizengine.meeting.model.dto.output;

import com.example.mywebquizengine.user.model.dto.UserCommonView;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface MeetingView {
    Long getMeetingId();

    @Value("#{T(com.example.mywebquizengine.common.utils.UserUtil).getUserForMeeting(target.firstUserId, target.secondUserId)}")
    UserCommonView getUser();

    Double getLng();
    Double getLat();
    Date getTime();

    @Value("#{@requestService.isPossibleToSendRequest(target.meetingId)}")
    boolean isPossibleToSendRequest();
}
