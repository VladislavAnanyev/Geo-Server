package com.example.meetings.user.model.domain;

import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.photo.model.domain.Photo;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private String password;

    @Column(name = "description")
    private String description;

    @ColumnDefault("false")
    @Column(name = "online")
    private boolean online;

    @Column(name = "signin_via_phone_code_expiration")
    private LocalDateTime signInViaPhoneCodeExpiration;

    @Column(name = "status")
    private boolean status;

    @OneToOne
    @JoinColumn(name = "main_photo_id")
    private Photo mainPhoto;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private List<Photo> photos = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "users_dialogs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "dialog_id")
    )
    private Set<Dialog> dialogs = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "users_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Device> devices = new ArrayList<>();

    public List<GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
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
}