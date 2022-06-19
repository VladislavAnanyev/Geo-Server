package com.example.mywebquizengine.model.geo.dto.output;

import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface MeetingView {

    Long getMeetingId();

    @Value("#{target.secondUser}")
    UserCommonView getUser();

    Double getLng();
    Double getLat();

    Date getTime();

    @Value("TRUE")
    boolean isPossibleToSendRequest();

}
