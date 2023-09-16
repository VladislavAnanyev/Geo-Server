package com.example.meetings.chat.model.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;

@Embeddable
@Data
@Accessors(chain = true)
public class MessageFile {
    private String originalName;
    private String filename;
    private String contentType;
    private String uri;
}
