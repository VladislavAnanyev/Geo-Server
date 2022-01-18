package com.example.mywebquizengine.model.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface LastDialog {

    @Value("#{target.dialogId}")
    Integer getDialogId();

    String getContent();

    @Value("#{target.status}")
    String getStatus();

    @Value("#{new com.example.mywebquizengine.model.userinfo.User(target.username, target.firstName, target.lastName, target.avatar, target.online)}")
    UserCommonView getLastSender();

    @Value("#{@messageService.getCompanionForLastDialogs(target.dialogId)}")
    String getName();

    @Value("#{@messageService.getCompanionAvatarForLastDialogs(target.dialogId)}")
    String getImage();

    Date getTimestamp();


}
