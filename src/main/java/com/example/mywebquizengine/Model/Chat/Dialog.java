package com.example.mywebquizengine.Model.Chat;

import com.example.mywebquizengine.Model.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "DIALOGS")
public class Dialog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long dialog_id;

    private String name;

    private String image;

    @ManyToMany(mappedBy = "dialogs", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "dialog")
    private List<Message> messages;

    public Long getDialog_id() {
        return dialog_id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setDialog_id(Long dialog_id) {
        this.dialog_id = dialog_id;
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
