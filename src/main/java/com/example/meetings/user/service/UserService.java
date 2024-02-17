package com.example.meetings.user.service;

import com.example.meetings.common.exception.GlobalErrorCode;
import com.example.meetings.common.exception.UserNotFoundException;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.model.dto.*;
import com.example.meetings.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public AuthUserView getAuthUserInfo(Long userId) {
        return userRepository.findAllByUserId(userId);
    }

    public List<UserCommonView> findMyFriends(Long userId) {
        return userRepository.findUsersByFriendsUserId(userId);
    }

    @Transactional
    public ProfileView getUserProfileById(Long userId) {
        return userRepository.findUserByUserIdOrderByUsernameAscPhotosAsc(userId);
    }

    @Transactional
    public void deleteFriend(Long userId, Long authUserId) {
        User user = loadUserByUserId(authUserId);
        User friend = loadUserByUserId(userId);
        user.removeFriend(friend);
    }

    public User loadUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
        }

        return user.get();
    }

    public User loadUserByUserIdProxy(Long userId) throws UsernameNotFoundException {
        return userRepository.getOne(userId);
    }
}
