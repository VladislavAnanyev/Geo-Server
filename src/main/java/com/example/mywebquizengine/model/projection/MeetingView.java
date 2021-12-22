package com.example.mywebquizengine.model.projection;

import java.util.Date;

public interface MeetingView {

    Long getId();

    UserCommonView getFirstUser();

    UserCommonView getSecondUser();

    Double getLng();
    Double getLat();

    Date getTime();

}
