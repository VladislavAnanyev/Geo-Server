package com.example.meetings.photo.model.domain;

import com.example.meetings.user.model.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "USERS_PHOTOS")
@Accessors(chain = true)
@Getter
@Setter
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

    @Override
    public int compareTo(Photo photo) {
        return this.position.compareTo(photo.position);
    }

}
