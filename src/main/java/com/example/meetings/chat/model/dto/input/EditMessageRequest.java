package com.example.meetings.chat.model.dto.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class EditMessageRequest {

    @NotNull
    @NotBlank
    String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
