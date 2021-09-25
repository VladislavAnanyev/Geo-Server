package com.example.mywebquizengine.Model.Projection;

import java.security.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public interface MessageView {
    String getContent();
    UserForMessageView getSender();
    ZonedDateTime getTimestamp();
    //DialogWithUsersView getDialog();
}
