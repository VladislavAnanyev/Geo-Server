package com.example.mywebquizengine.service.utils;

import com.example.mywebquizengine.model.chat.domain.Dialog;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.repos.DialogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;


import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class MessageViewLogicUtil {

    @Autowired
    private DialogRepository dialogRepository;

    public String getCompanion(String name, Set<User> users) {

        if (users.size() > 2) {
            return name;
        } else if (users.size() == 2) {
            Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
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
            Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
            Set<User> userSet = new HashSet<>(users);
            userSet.removeIf(user -> user.getUserId().equals(userId));
            return userSet.iterator().next().getPhotos().get(0).getUrl();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    public String getCompanionForLastDialogs(Long dialog_id) {
        System.out.println("Вызван комп фор ласт Ди");
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialog_id);
        if (optionalDialog.isPresent()) {
            Dialog dialog = optionalDialog.get();
            Set<User> users = dialog.getUsers();

            if (users.size() > 2) {
                return dialog.getName();
            } else if (users.size() == 2) {
                Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
                Set<User> userSet = new HashSet<>(users);
                userSet.removeIf(user -> user.getUserId().equals(userId));
                return userSet.iterator().next().getUsername();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else throw new EntityNotFoundException("Dialog not found");
    }


    public String getCompanionAvatarForLastDialogs(Long dialog_id) {
        System.out.println("Вызван комп ав фор ласт Ди");

        Optional<Dialog> optionalDialog = dialogRepository.findById(dialog_id);
        if (optionalDialog.isPresent()) {
            Dialog dialog = optionalDialog.get();
            Set<User> users = dialog.getUsers();

            if (users.size() > 2) {
                return dialog.getImage();
            } else if (users.size() == 2) {
                Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
                Set<User> userSet = new HashSet<>(users);
                userSet.removeIf(user -> user.getUserId().equals(userId));
                return userSet.iterator().next().getPhotos().get(0).getUrl();

            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else throw new EntityNotFoundException("Dialog not found");
    }

}
