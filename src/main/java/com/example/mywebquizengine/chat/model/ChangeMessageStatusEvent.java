package com.example.mywebquizengine.chat.model;

import com.example.mywebquizengine.chat.model.domain.Dialog;
import com.example.mywebquizengine.chat.model.domain.MessageStatus;
import com.example.mywebquizengine.chat.model.domain.Notifiable;
import com.example.mywebquizengine.user.model.domain.User;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class ChangeMessageStatusEvent implements Notifiable {
    private Dialog dialog;
    private MessageStatus status;
    @Override
    public Set<User> getUsersToSendNotification() {
        return dialog.getUsers();
    }
}
