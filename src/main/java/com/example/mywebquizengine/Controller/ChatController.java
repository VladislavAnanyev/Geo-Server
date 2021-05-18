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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import static com.example.mywebquizengine.Controller.QuizController.getAuthUser;

@Validated
@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;


    @GetMapping(path = "/chat")
    public String chat(Model model, Authentication authentication) {
        User user = getAuthUser(authentication, userService);
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogs(user.getUsername()));
        return "chat";
    }

    @GetMapping(path = "/chat/{username}")
    public String chatWithUser(Model model, @PathVariable String username, Authentication authentication) {
        User user = getAuthUser(authentication, userService);
        model.addAttribute("user", userService.reloadUser(username).get());
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogs(user.getUsername()));
        model.addAttribute("messages", messageService.
                getMessages(user.getUsername(), username));


        return "chat";
    }


    @PostMapping("/chat/{userName}")
    public void sendMessage(@PathVariable String userName, @RequestBody Message message, Authentication authentication) {
        User user = getAuthUser(authentication, userService);
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


        User user = userService.reloadUser(message.getSender().getUsername()).get();

        user.setTests(new ArrayList<>()); // костыль для корректного отображения (тесты не инициализируются автоматически)
        message.setSender(user);


        User recipient = userService.reloadUser(message.getRecipient().getUsername()).get();
        recipient.setTests(new ArrayList<>());
        message.setRecipient(recipient);

        message.setTimestamp(new GregorianCalendar());
        message.setStatus(MessageStatus.DELIVERED);
        messageService.saveMessage(message);
        //simpMessagingTemplate.convertAndSend("/topic/" + message.getRecipient().getUsername(), message);


        return message;
    }

    @GetMapping(path = "/error")
    public String handleError(){
        return "error";
    }




}
