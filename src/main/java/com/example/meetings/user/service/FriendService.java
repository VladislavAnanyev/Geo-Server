package com.example.meetings.user.service;

import com.example.meetings.user.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class FriendService {

    @Autowired
    private UserService userService;

    @Transactional
    public void makeFriends(Long firstUserId, Long secondUserId) {
        User firstUser = userService.loadUserByUserId(firstUserId);
        User secondUser = userService.loadUserByUserId(secondUserId);
        firstUser.addFriend(secondUser);
    }

    public boolean isFriends(Long fromUserId, Long toUserId) {
        User firstUser = userService.loadUserByUserId(fromUserId);
        return firstUser.getFriends().stream().map(User::getUserId).collect(Collectors.toSet()).contains(toUserId);
    }
}
