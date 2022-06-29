package com.example.mywebquizengine.user.service;

import com.example.mywebquizengine.user.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
