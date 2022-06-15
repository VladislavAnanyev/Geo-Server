package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.exception.AuthorizationException;
import com.example.mywebquizengine.model.exception.LogicException;
import com.example.mywebquizengine.model.exception.UserNotFoundException;
import com.example.mywebquizengine.model.projection.ProfileView;
import com.example.mywebquizengine.model.projection.UserCommonView;
import com.example.mywebquizengine.model.projection.UserView;
import com.example.mywebquizengine.model.userinfo.*;
import com.example.mywebquizengine.repos.UserRepository;
import com.example.mywebquizengine.service.utils.RabbitUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void updateUser(String lastName, String firstName, String username) {
        if (lastName != null && !lastName.trim().equals("") && firstName != null && !firstName.trim().equals("")) {
            userRepository.updateUserInfo(firstName, lastName, username);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public void activateAccount(String activationCode) {
        User user = userRepository.findByActivationCode(activationCode);
        if (user != null) {
            user.setStatus(true);
            userRepository.activateAccount(user.getUsername());
        }
    }

    public void getUserViaChangePasswordCode(String changePasswordCode) {

        Optional<User> optionalUser = userRepository.findByChangePasswordCode(changePasswordCode);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    public void updateBalance(Integer coins, String username) {
        User user = userRepository.findById(username).get();
        user.setBalance(user.getBalance() + coins);
    }

    public ArrayList<User> getUserList() {
        return (ArrayList<User>) userRepository.findAll();
    }

    public List<UserCommonView> findMyFriends(String username) {
        return userRepository.findUsersByFriendsUsername(username);
    }

    public UserCommonView getUserView(String username) {
        return userRepository.findByUsername(username);
    }

    public Boolean checkForExistUser(String username) {
        return userRepository.existsById(username);
    }

    @Transactional
    public ProfileView getUserProfileById(String username) {
        return userRepository.findUserByUsernameOrderByUsernameAscPhotosAsc(username);
    }

    @Transactional
    public void deleteFriend(String username, String authUsername) {
        User user = loadUserByUsername(authUsername);
        User friend = loadUserByUsername(username);
        user.removeFriend(friend);
    }

    public User loadUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(username);

        if (user.isPresent()) {
            return user.get();
        } else throw new EntityNotFoundException("Пользователь не найден");
    }

    public User loadUserByUsernameProxy(String username) throws UsernameNotFoundException {
        return userRepository.getOne(username);
    }
}
