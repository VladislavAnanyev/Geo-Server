package com.example.meetings.user.model.domain;

import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.photo.model.domain.Photo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "USERS")
@Getter
@Setter
public class User {

    private static final long serialVersionUID = -7422293274841574951L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)

    private Long userId;

    @NotBlank
    @NotNull
    private String username;

    @NotBlank
    @NotNull
    private String email;

    private String activationCode;

    private String changePasswordCode;

    @NotBlank
    @NotNull
    private String firstName;

    @NotBlank
    @NotNull
    private String lastName;

    @Size(min = 5)
    private String password;

    private String avatar;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private List<Photo> photos;

    private String description;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "users_dialogs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "dialog_id")
    )
    private Set<Dialog> dialogs = new HashSet<>();

    private boolean status;

    private Integer balance;

    @ManyToMany
    @JoinTable(name = "users_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(joinColumns = @JoinColumn(name = "user_id"))
    private List<Role> roles;

    @ColumnDefault("false")
    private boolean online;

    private Calendar signInViaPhoneCodeExpiration;

    @OneToMany(mappedBy = "user")
    private List<Device> devices;

    public User() {
    }

    public User(Long userId, String username, String firstName, String lastName, String avatar, boolean online) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        Photo photo = new Photo();
        photo.setUrl(avatar);
        photo.setPosition(0);
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);
        this.photos = photos;
        this.online = online;
    }

    public void grantAuthority(Role authority) {
        if (roles == null) roles = new ArrayList<>();
        this.roles.add(authority);
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> avatarName) {
        Photo photo = new Photo();
        photo.setUrl(avatarName.get(0));
        photo.setUser(this);
        photo.setPosition(0);
        this.photos = Collections.singletonList(photo);
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = new ArrayList<>();
        this.friends.addAll(friends);
    }

    public void addFriend(User user) {
        this.friends.add(user);
        user.getFriends().add(this);
    }

    public void removeFriend(User user) {
        this.friends.remove(user);
        user.getFriends().remove(this);
    }

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.toString())));
        return authorities;
    }
}