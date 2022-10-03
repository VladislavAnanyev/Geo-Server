package com.example.mywebquizengine.chat.model.dto.input;

import com.example.mywebquizengine.chat.model.FileResponse;
import com.example.mywebquizengine.chat.model.ForwardedMessagesRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Accessors(chain = true)
public class SendMessageRequest {

    @NotNull
    private Long dialogId;

    @NotNull
    @NotBlank
    private String content;

    @NotNull
    @NotBlank
    private String uniqueCode;

    private ForwardedMessagesRequest forwardedMessagesRequest;

    @Size(max = 5)
    private List<FileResponse> files;
}
