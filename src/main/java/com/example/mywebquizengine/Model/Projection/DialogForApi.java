package com.example.mywebquizengine.Model.Projection;

import java.util.Set;

public interface DialogForApi {
    Long getDialogId();
    String getName();
    String getImage();
    Set<UserForMessageView> getUsers();
}
