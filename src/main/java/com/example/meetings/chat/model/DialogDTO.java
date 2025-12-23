package com.example.meetings.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
public class DialogDTO {
    private Long dialogId;
    private String type;
    private String image;
    private Set<UserDto> users;
    private String name;
    private List<MessageDTO> messages;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String meta;
}
