package com.example.mywebquizengine.common.model;

import lombok.Data;

@Data
public class SuccessfulResponse implements Response {
    
    private String status;

    @Override
    public ResponseStatus getStatus() {
        return ResponseStatus.SUCCESS;
    }
}
