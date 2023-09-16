package com.example.meetings.chat.model.dto.input;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class CreateGroupRequest {
    @Size(min = 2)
    @NotNull
    private List<Long> users;

    private String name;

    public String getName() {
        return name;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    public void setName(String name) {
        this.name = name;
    }
}
