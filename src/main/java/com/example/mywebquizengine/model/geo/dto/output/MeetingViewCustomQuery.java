package com.example.mywebquizengine.model.geo.dto.output;

import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface MeetingViewCustomQuery {
    Long getMeetingId();

    @Value("#{T(com.example.mywebquizengine.service.utils.UserUtil).getUserForMeeting(target.firstUserId, target.secondUserId)}")
    UserCommonView getUser();

    Double getLng();
    Double getLat();
    Date getTime();

    @Value("#{@requestService.isPossibleToSendRequest(target.meetingId)}")
    boolean isPossibleToSendRequest();
}
