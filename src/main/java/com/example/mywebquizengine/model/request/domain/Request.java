package com.example.mywebquizengine.model.request.domain;

import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.model.chat.domain.Message;
import com.example.mywebquizengine.model.geo.domain.Meeting;

import javax.persistence.*;

@Entity(name = "REQUESTS")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private User to;

    private String status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long id) {
        this.requestId = id;
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

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
