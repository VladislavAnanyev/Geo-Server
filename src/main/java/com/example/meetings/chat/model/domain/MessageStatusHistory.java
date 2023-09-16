package com.example.meetings.chat.model.domain;

import com.example.meetings.user.model.domain.User;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Accessors(chain = true)
public class MessageStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long historyId;

    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Date timestamp;
}
