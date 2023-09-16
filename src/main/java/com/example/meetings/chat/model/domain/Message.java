package com.example.meetings.chat.model.domain;

import com.example.meetings.user.model.domain.User;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity(name = "MESSAGES")
@Data
@Accessors(chain = true)
public class Message implements Notifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private User sender;

    @Size(min = 1)
    @NotNull
    private String content;

    private Date timestamp;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dialog_id")
    private Dialog dialog;

    @ElementCollection
    @CollectionTable(
            name="MESSAGES_PHOTOS",
            joinColumns=@JoinColumn(name="MESSAGE_ID")
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
        return dialog.getUsers();
    }
}