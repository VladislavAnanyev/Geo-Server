package com.example.mywebquizengine.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ForwardedMessages {
    private List<Long> messagesId;
    private Long dialogId;
}
