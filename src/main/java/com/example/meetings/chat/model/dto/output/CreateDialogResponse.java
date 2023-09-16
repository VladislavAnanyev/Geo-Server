package com.example.meetings.chat.model.dto.output;

import com.example.meetings.common.model.SuccessfulResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDialogResponse extends SuccessfulResponse {
    @JsonProperty("result")
    private CreateDialogResult createDialogResult;
}
