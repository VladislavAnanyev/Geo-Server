package com.example.meetings.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Set;

@Data
@Accessors(chain = true)
public class LastDialogDTO {
    private Long dialogId;
    private String content;
    private UserDto lastSender;
    private String name;
    private String image;
    private Date timestamp;
    private Integer unreadMessages;
    private Set<UserDto> users;
    private String type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String meta;
}
