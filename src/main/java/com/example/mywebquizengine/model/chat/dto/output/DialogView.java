package com.example.mywebquizengine.model.chat.dto.output;

import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Set;

public interface DialogView {
    Long getDialogId();

    @Value("#{@messageViewLogicUtil.getCompanionAvatar(target.image ,target.users)}")
    String getImage();

    Set<UserCommonView> getUsers();

    @Value("#{@messageViewLogicUtil.getCompanion(target.name ,target.users)}")
    String getName();

    List<MessageView> getMessages();
}
