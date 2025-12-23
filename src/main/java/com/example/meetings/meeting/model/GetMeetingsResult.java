package com.example.meetings.meeting.model;

import com.example.meetings.meeting.model.dto.output.MeetingView;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GetMeetingsResult {
    private List<MeetingView> meetings;
}
