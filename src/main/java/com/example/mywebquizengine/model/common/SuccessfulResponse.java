package com.example.mywebquizengine.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;

public class SuccessfulResponse implements Response {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object result;

    public SuccessfulResponse() {}

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public ResponseStatus getStatus() {
        return ResponseStatus.SUCCESS;
    }
}
