package com.example.mywebquizengine.Model.Projection;

import java.util.List;
import java.util.Set;

public interface DialogWithUsersView {
    Long getDialogId();
    String getName();
    String getImage();
    Set<UserCommonView> getUsers();
    List<MessageView> getMessages();
}
