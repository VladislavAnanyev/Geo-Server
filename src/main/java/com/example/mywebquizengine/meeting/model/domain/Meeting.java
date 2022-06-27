package com.example.mywebquizengine.meeting.model.domain;

import com.example.mywebquizengine.request.model.domain.Request;
import com.example.mywebquizengine.user.model.domain.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "MEETINGS")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long meetingId;

    @ManyToOne
    @JoinColumn(name = "first_user_id")
    private User firstUser;

    @ManyToOne
    @JoinColumn(name = "second_user_id")
    private User secondUser;

    @OneToOne(mappedBy = "meeting")
    private Request request;

    private Double lat;
    private Double lng;

    private Date time;

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long id) {
        this.meetingId = id;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public Date getTime() {
        return time;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public List<User> getUsers() {
        return List.of(firstUser, secondUser);
    }

}
