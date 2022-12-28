package com.example.mywebquizengine.request;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import com.example.mywebquizengine.request.AcceptRequestResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AcceptRequestResponse extends SuccessfulResponse {
    private AcceptRequestResult result;
}
