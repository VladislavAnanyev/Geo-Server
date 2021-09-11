package com.example.mywebquizengine.Model.Chat;

import com.example.mywebquizengine.Model.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "DIALOGS")
public class Dialog {

    @Id
    @Column(name = "DIALOG_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String image;

    @JsonManagedReference
    @ManyToMany(mappedBy = "dialogs", cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "dialog")
    private List<Message> messages;

    public Long getId() {
        return id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setId(Long dialog_id) {
        this.id = dialog_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        this.users.add(user);
        user.getDialogs().add(this);
    }

    public void setUsers(Set<User> users) {

        this.users = new HashSet<>();
        this.users.addAll(users);
        //this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
