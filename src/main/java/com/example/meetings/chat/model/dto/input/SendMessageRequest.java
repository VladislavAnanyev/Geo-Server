package com.example.meetings.chat.model.dto.input;

import com.example.meetings.chat.model.FileResponse;
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

    @Data
    @Accessors(chain = true)
    public static class ForwardedMessagesRequest {
        @NotNull
        @Size(min = 1)
        private List<Long> messagesId;

        @NotNull
        private Long dialogId;
    }

}
