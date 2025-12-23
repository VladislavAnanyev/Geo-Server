package com.example.meetings.chat.service;

import com.example.meetings.user.model.dto.UserCommonView;
import lombok.Data;

import java.util.Date;

@Data
public class NewLastDialog {
    private Long dialogId;
    private String content;
    private String status;
    private UserCommonView lastSender;
    private String name;
    private String image;
    private Date timestamp;
}
