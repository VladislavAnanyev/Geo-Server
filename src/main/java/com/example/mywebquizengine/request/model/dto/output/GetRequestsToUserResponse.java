package com.example.mywebquizengine.request.model.dto.output;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetRequestsToUserResponse extends SuccessfulResponse {
    private GetRequestsToUserResult result;
}
