package com.example.mywebquizengine.common.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class SuccessfulResponse implements Response {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object result;

    @Override
    public ResponseStatus getStatus() {
        return ResponseStatus.SUCCESS;
    }
}
