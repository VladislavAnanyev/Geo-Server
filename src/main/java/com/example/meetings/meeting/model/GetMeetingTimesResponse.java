package com.example.meetings.meeting.model;

import com.example.meetings.common.model.SuccessfulResponse;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Модель для ответа на запрос получения обезличенной истории встреч")
@AllArgsConstructor
@Data
public class GetMeetingTimesResponse extends SuccessfulResponse {
    private GetMeetingTimesResult result;
}
