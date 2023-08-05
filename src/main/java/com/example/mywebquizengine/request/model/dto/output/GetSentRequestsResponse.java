package com.example.mywebquizengine.request.model.dto.output;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(value = "Модель для ответа на запрос получения отправленных заявок")
@Data
@AllArgsConstructor
public class GetSentRequestsResponse extends SuccessfulResponse {
    private GetSentFromUserRequestsResult result;
}
