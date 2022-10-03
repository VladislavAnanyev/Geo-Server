package com.example.mywebquizengine.chat.service;

import com.example.mywebquizengine.user.model.dto.UserCommonView;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

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
