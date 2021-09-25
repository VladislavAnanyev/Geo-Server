package com.example.mywebquizengine.Model.Chat;

import com.example.mywebquizengine.Model.Projection.UserForMessageView;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.MywebquizengineApplication;
import com.example.mywebquizengine.Repos.DialogRepository;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "DIALOGS")
public class Dialog  {

    @Id
    @Column(name = "DIALOG_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long dialogId;

    private String name;

    private String image;

    public Dialog() {}

    public Dialog(Long dialogId, String name, String image) {
        this.dialogId = dialogId;
        this.name = name;
        this.image = image;

        DialogRepository dialogRepository = MywebquizengineApplication.ctx.getBean(DialogRepository.class);
        this.users.clear();
        this.users.addAll(dialogRepository.findById(dialogId).get().getUsers());

    }

    @ManyToMany(mappedBy = "dialogs", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<User> users = new HashSet<>();


    @OneToMany(mappedBy = "dialog", fetch = FetchType.LAZY)
    private List<Message> messages;

    public Long getDialogId() {
        return dialogId;
    }

    public List<Message> getMessages() {
            return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setDialogId(Long dialog_id) {
        this.dialogId = dialog_id;
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