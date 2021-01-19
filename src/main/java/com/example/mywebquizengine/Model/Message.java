package com.example.mywebquizengine.Model;


import javax.persistence.Id;
import java.util.Calendar;

public class Message {
    @Id
    private Long id;
    private User sender;
    private User recipient;
    private String content;
    private Calendar timestamp;
    private MessageStatus status;
}
