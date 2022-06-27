package com.example.mywebquizengine.chat.model.dto.input;

import com.example.mywebquizengine.chat.model.domain.Dialog;
import com.example.mywebquizengine.user.model.domain.User;

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
