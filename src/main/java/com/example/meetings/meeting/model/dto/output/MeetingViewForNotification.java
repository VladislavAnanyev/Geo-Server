package com.example.meetings.meeting.model.dto.output;

import com.example.meetings.user.model.dto.UserCommonView;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;

public interface MeetingViewForNotification {
    Long getMeetingId();
    @Value("#{target.getUsers()}")
    List<UserCommonView> getUsers();
    Double getLng();
    Double getLat();
    Date getTime();
    @Value("TRUE")
    boolean isPossibleToSendRequest();
}
