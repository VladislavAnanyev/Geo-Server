package com.example.mywebquizengine.Model.Projection;

import com.example.mywebquizengine.Model.Projection.UserCommonView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

public interface LastDialog {

    @Value("#{target.dialogId}")
    Integer getDialogId();

    String getContent();

    @Value("#{new com.example.mywebquizengine.Model.User(target.username, target.firstName, target.lastName, target.avatar)}")
    UserCommonView getLastSender();

    @Value("#{@messageService.getCompanionForLastDialogs(target.dialogId)}")
    String getName();

    @Value("#{@messageService.getCompanionAvatarForLastDialogs(target.dialogId)}")
    String getImage();

    //@Value("#{new java.util.Date(target.timestamp.getTime())}")
    Date getTimestamp();

    /*@Value("#{new com.example.mywebquizengine.Model.Chat.Dialog(target.dialogId, target.name, target.image, @dialogRepository.findById(T(Long).parseLong(target.dialogId)).get().getUsers())}")
    DialogWithoutMessages getDialog();*/

    /*@Value("#{new com.example.mywebquizengine.Model.Chat.Dialog(target.dialogId, target.name, target.image)}")
    DialogForApi getDialog();*/
    //Long getDialogId();
}
