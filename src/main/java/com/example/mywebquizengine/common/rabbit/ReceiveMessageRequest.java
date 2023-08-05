package com.example.mywebquizengine.common.rabbit;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ReceiveMessageRequest {
    @NotNull
    private Long dialogId;
}

