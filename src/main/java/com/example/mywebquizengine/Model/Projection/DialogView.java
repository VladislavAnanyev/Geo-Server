package com.example.mywebquizengine.Model.Projection;

import com.example.mywebquizengine.Model.Chat.Message;
import com.example.mywebquizengine.Model.User;

import java.util.List;
import java.util.Set;

public interface DialogView {
    Long getId();
    String getName();
    String getImage();
    Set<UserView> getUsers();
    List<MessageView> getMessages();
}
