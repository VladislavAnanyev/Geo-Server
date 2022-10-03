package com.example.mywebquizengine.chat.model.domain;

import com.example.mywebquizengine.user.model.domain.User;

import java.util.Set;

public interface Notifiable {
    Set<User> getUsersToSendNotification();
}
