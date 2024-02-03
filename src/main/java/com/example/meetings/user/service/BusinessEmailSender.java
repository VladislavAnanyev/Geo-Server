package com.example.meetings.user.service;

import com.example.meetings.common.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BusinessEmailSender {

    @Autowired
    private MailSender mailSender;
    @Value("${hostname}")
    private String hostname;

}
