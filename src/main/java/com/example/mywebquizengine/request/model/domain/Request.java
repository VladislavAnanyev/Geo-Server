package com.example.mywebquizengine.request.model.domain;

import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.chat.model.domain.Message;
import com.example.mywebquizengine.meeting.model.domain.Meeting;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "REQUESTS")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private User to;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

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

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public RequestStatus getStatus() {
        return status;
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

    public Set<User> getUsers() {
        return Set.of(sender, to);
    }
}
