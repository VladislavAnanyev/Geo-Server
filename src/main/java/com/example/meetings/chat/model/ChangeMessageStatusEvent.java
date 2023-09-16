package com.example.meetings.chat.model;

import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.chat.model.domain.MessageStatus;
import com.example.meetings.chat.model.domain.Notifiable;
import com.example.meetings.user.model.domain.User;
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
