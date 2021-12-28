package com.example.mywebquizengine.model.projection;

import com.example.mywebquizengine.model.Photo;

import java.util.List;

public interface ProfileView {
    String getUsername();
    String getFirstName();
    String getLastName();
    List<Photo> getPhotos();
}
