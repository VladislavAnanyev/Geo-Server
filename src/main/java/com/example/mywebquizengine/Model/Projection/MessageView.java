package com.example.mywebquizengine.Model.Projection;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public interface MessageView {
    Integer getId();
    String getContent();
    UserCommonView getSender();
    //ZonedDateTime getTimestamp();
    Date getTimestamp();
    //DialogWithUsersView getDialog();
}
