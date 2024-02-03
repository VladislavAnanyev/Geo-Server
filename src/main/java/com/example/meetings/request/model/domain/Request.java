package com.example.meetings.request.model.domain;

import com.example.meetings.chat.model.domain.Message;
import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.user.model.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "REQUESTS")
@Getter
@Setter
@Accessors(chain = true)
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

    public Set<User> getUsers() {
        return Set.of(sender, to);
    }
}
