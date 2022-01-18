package com.example.mywebquizengine.model.chat;

import com.example.mywebquizengine.model.userinfo.User;

public class Typing {
    private User user;
    private Long dialogId;

    public Typing() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }
}
