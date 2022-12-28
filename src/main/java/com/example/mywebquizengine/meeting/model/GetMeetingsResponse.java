package com.example.mywebquizengine.meeting.model;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(value = "Модель для ответа на запрос получения встреч")
@AllArgsConstructor
@Data
public class GetMeetingsResponse extends SuccessfulResponse {
    private GetMeetingsResult result;
}
