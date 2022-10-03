package com.example.mywebquizengine.chat.model.dto.input;

import com.example.mywebquizengine.chat.model.domain.Dialog;
import com.example.mywebquizengine.chat.model.domain.Notifiable;
import com.example.mywebquizengine.user.model.domain.User;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class Typing implements Notifiable {
    private User user;
    private Dialog dialog;

    @Override
    public Set<User> getUsersToSendNotification() {
        Set<User> users = new HashSet<>(dialog.getUsers());
        users.remove(user);
        return users;
    }
}
