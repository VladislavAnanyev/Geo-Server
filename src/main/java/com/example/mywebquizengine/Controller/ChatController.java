package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.Chat.Message;
import com.example.mywebquizengine.Model.Chat.MessageStatus;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Service.MessageService;
import com.example.mywebquizengine.Service.TestService;
import com.example.mywebquizengine.Service.UserService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.GregorianCalendar;

@Validated
@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private TestService testService;

    @GetMapping(path = "/chat")
    public String chat(Model model) {
        //model.addAttribute("user", userService.reloadUser(username).get());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogs(user.getUsername()));
        return "chat";
    }

    @GetMapping(path = "/chat/{username}")
    public String chatWithUser(Model model, @PathVariable String username) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", userService.reloadUser(username).get());
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogs(user.getUsername()));
        model.addAttribute("messages", messageService.
                getMessages(user.getUsername(), username));

        /*for (int i = 0; i < messageService.
                getMessages(userService.reloadUser(username).get().getUsername(), username).size(); i++) {
            System.out.println(messageService.
                    getMessages(userService.reloadUser(username).get().getUsername(), username).get(i));
        }*/

        return "chat";
    }


    @PostMapping("/chat/{userName}")
    public void sendMessage(@PathVariable String userName, @RequestBody Message message) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        message.setSender(user);
        message.setRecipient(userService.reloadUser(userName).get());
        message.setTimestamp(new GregorianCalendar());
        message.setStatus(MessageStatus.DELIVERED);
        messageService.saveMessage(message);
        //simpMessagingTemplate.convertAndSend("/topic/messages/" + userName, message);
    }

    @Modifying
    @Transactional
    @MessageMapping("/user/{userId}")
    @SendTo("/topic/{userId}")
    public Message sendTestMessage(@Payload Message message) {

        //recip = message.getRecipient().getUsername();
        User user = userService.reloadUser(message.getSender().getUsername()).get();
        //Hibernate.initialize(user.getTests());
        user.setTests(/*testService.getMyQuizNoPaging(user.getUsername()*/new ArrayList<>());
        message.setSender(user);


        User recipient = userService.reloadUser(message.getRecipient().getUsername()).get();
        recipient.setTests(/*testService.getMyQuizNoPaging(recipient.getUsername())*/new ArrayList<>());
        message.setRecipient(recipient);

        message.setTimestamp(new GregorianCalendar());
        message.setStatus(MessageStatus.DELIVERED);
        messageService.saveMessage(message);
        //simpMessagingTemplate.convertAndSend("/topic/" + message.getRecipient().getUsername(), message);


        return message;
    }

    @GetMapping(path = "/testch")
    public String getChat(){
        return "testchat";
    }




}
