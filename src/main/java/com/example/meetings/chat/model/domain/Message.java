package com.example.meetings.chat.model.domain;

import com.example.meetings.user.model.domain.User;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.*;

@Entity(name = "MESSAGES")
@Data
@Accessors(chain = true)
public class Message implements Notifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id")
    private User sender;

    @Column(name = "content")
    private String content;

    @Column(name = "timestamp")
    private Date timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dialog_id")
    private Dialog dialog;

    @ElementCollection
    @CollectionTable(
            name = "MESSAGES_PHOTOS",
            joinColumns = @JoinColumn(name = "MESSAGE_ID")
    )
    private List<MessageFile> files;

    @OneToMany
    @JoinTable(name = "messages_forwarded",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "fowarded_message_id")
    )
    private List<Message> forwardedMessages;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageStatusHistory> messageStatusHistoryList;

    @Transient
    private String uniqueCode;

    @Transient
    private String type;

    @Override
    public Set<User> getUsersToSendNotification() {
        Set<User> users = new HashSet<>(dialog.getUsers());
        users.remove(sender);

        return users;
    }
}