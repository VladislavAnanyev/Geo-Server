package com.example.meetings.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class SendMessageModel {
    @NotNull
    private Long senderId;
    @NotNull
    private Long dialogId;
    @NotNull
    @NotBlank
    private String content;
    @NotNull
    @NotBlank
    private String uniqueCode;
    private ForwardedMessages forwardedMessages;
    private List<FileResponse> files;
}
