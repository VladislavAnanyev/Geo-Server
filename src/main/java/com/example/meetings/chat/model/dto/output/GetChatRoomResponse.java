package com.example.meetings.chat.model.dto.output;

import com.example.meetings.common.model.SuccessfulResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "Модель для ответа на запрос получения диалога по идентификатору")
public class GetChatRoomResponse extends SuccessfulResponse {
    @ApiModelProperty(value = "Информация о диалоге")
    @JsonProperty("result")
    private DialogView dialog;
}
