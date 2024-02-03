package com.example.meetings.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageFileDTO {
    private String originalName;
    private String contentType;
    private String uri;
}
