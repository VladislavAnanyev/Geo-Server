package com.example.mywebquizengine.model.rabbit;

import com.example.mywebquizengine.model.User;

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
