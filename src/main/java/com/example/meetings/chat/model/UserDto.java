package com.example.meetings.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserDto {
    private Long userId;
    @JsonProperty("username")
    private String login;
    private String firstName;
    private boolean online;
    private String avatar;
    private String meta;
}
