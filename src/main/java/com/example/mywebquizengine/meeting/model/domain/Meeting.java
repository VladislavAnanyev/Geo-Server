package com.example.mywebquizengine.meeting.model.domain;

import com.example.mywebquizengine.request.model.domain.Request;
import com.example.mywebquizengine.user.model.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "MEETINGS")
@Getter
@Setter
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

    private LocalDateTime time;

    public List<User> getUsers() {
        return List.of(firstUser, secondUser);
    }

}
