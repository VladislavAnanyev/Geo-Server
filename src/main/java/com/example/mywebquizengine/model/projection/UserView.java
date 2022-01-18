package com.example.mywebquizengine.model.projection;

import com.example.mywebquizengine.model.userinfo.Photo;
import com.example.mywebquizengine.model.userinfo.Role;

import java.util.List;

public interface UserView {
    String getUsername();
    String getFirstName();
    String getLastName();
    List<Photo> getPhotos();
    String getEmail();
    Integer getBalance();
    List<Role> getRoles();
    boolean isStatus();
    String getOnline();
}
