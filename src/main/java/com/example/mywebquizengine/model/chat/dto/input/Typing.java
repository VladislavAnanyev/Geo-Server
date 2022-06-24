package com.example.mywebquizengine.model.chat.dto.input;

import com.example.mywebquizengine.model.chat.domain.Dialog;
import com.example.mywebquizengine.model.userinfo.domain.User;

public class Typing {
    private User user;
    private Dialog dialog;

    public Typing() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }
}
