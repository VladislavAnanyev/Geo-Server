package com.example.mywebquizengine.model.chat.domain;

import com.example.mywebquizengine.model.userinfo.domain.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity(name = "MESSAGES")
public class Message {

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
    private List<MessagePhoto> photos;

    @OneToMany
    @JoinTable(name = "messages_forwarded",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "fowarded_message_id")
    )
    private List<Message> forwardedMessages;

    @Transient
    private String uniqueCode;

    @Transient
    private String type;

    public Message() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long id) {
        this.messageId = id;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public List<Message> getForwardedMessages() {
        return forwardedMessages;
    }

    public void setForwardedMessages(List<Message> forwardedMessages) {
        this.forwardedMessages = forwardedMessages;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public List<MessagePhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<MessagePhoto> photos) {
        this.photos = photos;
    }
}