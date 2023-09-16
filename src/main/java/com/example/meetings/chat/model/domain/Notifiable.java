package com.example.meetings.chat.model.domain;

import com.example.meetings.user.model.domain.User;

import java.util.Set;

public interface Notifiable {
    Set<User> getUsersToSendNotification();
}
