package com.example.meetings.request.model.dto.output;

import com.example.meetings.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AcceptRequestResponse extends SuccessfulResponse {
    private AcceptRequestResult result;
}
