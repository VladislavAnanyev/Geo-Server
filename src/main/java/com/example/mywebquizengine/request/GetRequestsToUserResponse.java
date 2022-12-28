package com.example.mywebquizengine.request;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import com.example.mywebquizengine.request.GetRequestsToUserResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetRequestsToUserResponse extends SuccessfulResponse {
    private GetRequestsToUserResult result;
}
