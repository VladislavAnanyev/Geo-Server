package com.example.mywebquizengine.model.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Set;

public interface DialogView {
    Long getDialogId();

    @Value("#{@messageService.getCompanionAvatar(target.image ,target.users)}")
    String getImage();

    Set<UserCommonView> getUsers();

    @Value("#{@messageService.getCompanion(target.name ,target.users)}")
    String getName();

    //@Value("#{T(com.google.common.collect.Lists).reverse(@messageRepository.findAllByDialog_DialogIdAndStatusNot(target.dialogId, T(com.example.mywebquizengine.model.chat.MessageStatus).DELETED, target.paging).content)}")
    @Value("#{@messageService.getListOfMessages(target.dialogId, target.paging)}")
    List<MessageView> getMessages();
}
