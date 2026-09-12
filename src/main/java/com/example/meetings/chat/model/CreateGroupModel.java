package com.example.meetings.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain=true)
@Data
public class CreateGroupModel {
    private String name;
    private List<Long> users;
    private Long authUserId;
    private String image;
}
