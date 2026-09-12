package com.example.meetings.meeting.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class GetMeetingTimesResult {
    private List<Date> times;
}
