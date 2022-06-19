package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.exception.GlobalErrorCode;
import com.example.mywebquizengine.model.exception.UserNotFoundException;
import com.example.mywebquizengine.model.userinfo.dto.output.AuthUserView;
import com.example.mywebquizengine.model.userinfo.dto.output.ProfileView;
import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void updateUser(String lastName, String firstName, Long userId) {
        if (lastName != null && !lastName.trim().equals("") && firstName != null && !firstName.trim().equals("")) {
            userRepository.updateUserInfo(firstName, lastName, userId);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public AuthUserView getAuthUser(Long userId) {
        return userRepository.findAllByUserId(userId);
    }

    public void activateAccount(String activationCode) {
        User user = userRepository.findByActivationCode(activationCode);
        if (user != null) {
            user.setStatus(true);
            userRepository.activateAccount(user.getUserId());
        }
    }

    public void getUserViaChangePasswordCode(String changePasswordCode) {

        Optional<User> optionalUser = userRepository.findByChangePasswordCode(changePasswordCode);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    public void updateBalance(Integer coins, Long userId) {
        User user = userRepository.findById(userId).get();
        user.setBalance(user.getBalance() + coins);
    }

    public ArrayList<User> getUserList() {
        return (ArrayList<User>) userRepository.findAll();
    }

    public List<UserCommonView> findMyFriends(Long userId) {
        return userRepository.findUsersByFriendsUserId(userId);
    }

    public UserCommonView getUserView(Long userId) {
        return userRepository.findByUserId(userId);
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

        if (user.isPresent()) {
            return user.get();
        } else throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
    }

    public User loadUserByUserIdProxy(Long userId) throws UsernameNotFoundException {
        return userRepository.getOne(userId);
    }
}
