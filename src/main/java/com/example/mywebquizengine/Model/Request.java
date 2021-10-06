package com.example.mywebquizengine.Model;

import com.example.mywebquizengine.Model.Chat.Message;
import com.example.mywebquizengine.Model.Geo.Geolocation;
import com.example.mywebquizengine.Model.Geo.Meeting;

import javax.persistence.*;

@Entity(name = "REQUESTS")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User to;

    private String status;

    @OneToOne
    private Meeting meeting;

    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User from) {
        this.sender = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
