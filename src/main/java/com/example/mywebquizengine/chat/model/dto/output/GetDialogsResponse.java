package com.example.mywebquizengine.chat.model.dto.output;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("Модель для ответа на запрос получения диалогов")
public class GetDialogsResponse extends SuccessfulResponse {
    @ApiModelProperty(value = "Информация о диалогах")
    @JsonProperty("result")
    private GetDialogsResult getDialogsResult;
}
