package com.example.meetings.chat.model.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@Accessors(chain = true)
public class MessageFile {
    private String originalName;
    private String filename;
    private String contentType;
    private String uri;
}
