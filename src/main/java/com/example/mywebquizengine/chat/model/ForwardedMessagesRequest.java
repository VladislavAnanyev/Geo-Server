package com.example.mywebquizengine.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Accessors(chain = true)
public class ForwardedMessagesRequest {
    @NotNull
    @Size(min = 1)
    private List<Long> messagesId;

    @NotNull
    private Long dialogId;
}
