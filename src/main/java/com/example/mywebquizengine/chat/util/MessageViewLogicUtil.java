package com.example.mywebquizengine.chat.util;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.chat.model.domain.Dialog;
import com.example.mywebquizengine.chat.model.domain.Message;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.chat.repository.DialogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;


import javax.persistence.EntityNotFoundException;
import java.util.*;

@Component
public class MessageViewLogicUtil {

    @Autowired
    private DialogRepository dialogRepository;

    public String getCompanion(String name, Set<User> users) {
        if (users.size() > 2) {
            return name;
        } else if (users.size() == 2) {
            Long userId = ((AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
            Set<User> userSet = new HashSet<>(users);
            userSet.removeIf(user -> user.getUserId().equals(userId));
            return userSet.iterator().next().getUsername();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public String getCompanionAvatar(String image, Set<User> users) {
        if (users.size() > 2) {
            return image;
        } else if (users.size() == 2) {
            Long userId = ((AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
            Set<User> userSet = new HashSet<>(users);
            userSet.removeIf(user -> user.getUserId().equals(userId));
            return userSet.iterator().next().getAvatar();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    public String getCompanionForLastDialogs(Long dialog_id) {
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialog_id);
        if (optionalDialog.isPresent()) {
            Dialog dialog = optionalDialog.get();
            Set<User> users = dialog.getUsers();

            if (users.size() > 2) {
                return dialog.getName();
            } else if (users.size() == 2) {
                Long userId = ((AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
                Set<User> userSet = new HashSet<>(users);
                userSet.removeIf(user -> user.getUserId().equals(userId));
                return userSet.iterator().next().getUsername();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else throw new EntityNotFoundException("Dialog not found");
    }


    public String getCompanionAvatarForLastDialogs(Long dialog_id) {
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialog_id);
        if (optionalDialog.isPresent()) {
            Dialog dialog = optionalDialog.get();
            Set<User> users = dialog.getUsers();

            if (users.size() > 2) {
                return dialog.getImage();
            } else if (users.size() == 2) {
                Long userId = ((AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
                Set<User> userSet = new HashSet<>(users);
                userSet.removeIf(user -> user.getUserId().equals(userId));
                return userSet.iterator().next().getAvatar();

            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else throw new EntityNotFoundException("Dialog not found");
    }
}
