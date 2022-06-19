package com.example.mywebquizengine.model.chat.dto.output;

import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface LastDialog {

    @Value("#{target.dialogId}")
    Integer getDialogId();

    String getContent();

    @Value("#{target.status}")
    String getStatus();

    @Value("#{new com.example.mywebquizengine.model.userinfo.domain.User(target.userId, target.username, target.firstName, target.lastName, target.avatar, target.online)}")
    UserCommonView getLastSender();

    @Value("#{@messageViewLogicUtil.getCompanionForLastDialogs(target.dialogId)}")
    String getName();

    @Value("#{@messageViewLogicUtil.getCompanionAvatarForLastDialogs(target.dialogId)}")
    String getImage();

    Date getTimestamp();


}
