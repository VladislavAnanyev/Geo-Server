package com.example.mywebquizengine.model.userinfo;

import com.example.mywebquizengine.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "USERS_PHOTOS")
public class Photo /*implements Comparable<Photo>*/ {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String url;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static List<Photo> getList(String url) {
        Photo photo = new Photo();
        photo.setUrl(url);
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);
        return photos;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

/*    @Override
    public int compareTo(Photo photo) {
        return this.position.compareTo(photo.position);
    }*/
}
