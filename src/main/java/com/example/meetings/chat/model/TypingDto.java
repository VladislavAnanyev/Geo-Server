package com.example.meetings.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TypingDto {
    private Long userId;
    private String username;
    private String firstName;
    private String meta;
    private Long dialogId;
}
