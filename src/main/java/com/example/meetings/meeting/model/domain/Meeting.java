package com.example.meetings.meeting.model.domain;

import com.example.meetings.request.model.domain.Request;
import com.example.meetings.user.model.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "MEETINGS")
@Getter
@Setter
@Accessors(chain = true)
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
