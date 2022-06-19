package com.example.mywebquizengine.model.geo.domain;

import com.example.mywebquizengine.model.request.domain.Request;
import com.example.mywebquizengine.model.userinfo.domain.User;

import javax.persistence.*;
import java.util.Date;

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

}
