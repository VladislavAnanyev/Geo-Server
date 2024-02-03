package com.example.meetings.chat.model.domain;

import com.example.meetings.user.model.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import javax.persistence.*;
import java.util.*;

@Entity(name = "DIALOGS")
@Getter
@Setter
public class Dialog {

    @Id
    @Column(name = "dialog_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long dialogId;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DialogType type;

    @OneToOne
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @ManyToMany(mappedBy = "dialogs", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @Transient
    @JsonIgnore
    private Pageable paging;

    @OneToMany(mappedBy = "dialog", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Message> messages = new ArrayList<>();

    public void addUser(User user) {
        this.users.add(user);
        user.getDialogs().add(this);
    }
}