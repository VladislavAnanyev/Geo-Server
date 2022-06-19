package com.example.mywebquizengine.model.userinfo.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "USERS_PHOTOS")
public class Photo implements Comparable<Photo> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long photoId;

    private String url;

    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
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

    public Long getPhotoId() {
        return photoId;
    }

    public String getUrl() {
        return url;
    }

    public void setPhotoId(Long id) {
        this.photoId = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public int compareTo(Photo photo) {
        return this.position.compareTo(photo.position);
    }
}
