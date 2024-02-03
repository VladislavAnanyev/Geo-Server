package com.example.meetings.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class MessageDTO {
    private Long messageId;
    private String content;
    private UserDto sender;
    private Date timestamp;
    private List<MessageDTO> forwardedMessages;
    private List<MessageFileDTO> files;
    private Long dialogId;
    private String uniqueCode;
    private String type;
    private String status;
    private List<Long> alreadyReadUserIds;
}
