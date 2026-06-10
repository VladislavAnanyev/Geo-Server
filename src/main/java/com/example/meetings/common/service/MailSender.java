package com.example.meetings.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Отправка простых текстовых писем через Spring JavaMailSender.
 */
@Service
public class MailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    /**
     * Отправляет письмо на указанный адрес.
     *
     * @param emailTo адрес получателя
     * @param subject тема письма
     * @param message текст письма
     */
    public void send(String emailTo, String subject, String message) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(username);
            mailMessage.setTo(emailTo);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailSender.send(mailMessage);
    }

}
