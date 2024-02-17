package com.example.meetings.chat.util;

import com.example.meetings.chat.model.CreateGroupModel;
import com.example.meetings.chat.model.dto.input.CreateGroupRequest;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupModelMapper {

    public static CreateGroupModel map(CreateGroupRequest request, Long userId) {
        CreateGroupModel createGroupModel = new CreateGroupModel();
        createGroupModel.setName(request.getName());
        List<Long> users = request.getUsers();
        users.add(userId);
        createGroupModel.setUsers(new ArrayList<>(users));
        return createGroupModel;
    }
}
