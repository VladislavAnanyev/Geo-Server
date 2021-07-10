package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.Chat.Message;
import com.example.mywebquizengine.Model.Chat.MessageStatus;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Service.MessageService;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.example.mywebquizengine.Controller.UserController.getAuthUser;

@Validated
@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


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
        model.addAttribute("user", userService.reloadUser(username));
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogs(user.getUsername()));
        model.addAttribute("messages", messageService.
                getMessages(user.getUsername(), username));


        return "chat";
    }


    @Modifying
    @Transactional
    @MessageMapping("/user/{userId}")
    @SendTo("/topic/{userId}")
    public Message sendMessage(@Payload Message message) {

        User user = userService.reloadUser(message.getSender().getUsername());

        // Persistence Bag. Используется костыль
        // для корректного отображения (тесты не инициализируются автоматически)
        //.setTests(new ArrayList<>());

        message.setSender(user);

        User recipient = userService.reloadUser(message.getRecipient().getUsername());
        //recipient.setTests(new ArrayList<>());
        message.setRecipient(recipient);


        // Устанавливается часовой пояс для хранения времени в БД постоянно по Москве
        // В БД будет сохраняться Московское время независимо от местоположения сервера/пользователя
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
        Calendar nowDate = new GregorianCalendar();
        nowDate.setTimeZone(timeZone);
        message.setTimestamp(nowDate);;

        message.setStatus(MessageStatus.DELIVERED);
        messageService.saveMessage(message);

        //simpMessagingTemplate.convertAndSend("/topic/application", message);

        return message;
    }

    @GetMapping(path = "/error")
    public String handleError(){
        return "error";
    }

}
