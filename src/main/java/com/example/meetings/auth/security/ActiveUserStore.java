package com.example.meetings.auth.security;

import java.util.ArrayList;
import java.util.List;

public class ActiveUserStore {

    public List<Long> users;

    public ActiveUserStore() {
        users = new ArrayList<>();
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

}