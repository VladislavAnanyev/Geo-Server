package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.Chat.Group;
import com.example.mywebquizengine.Model.Chat.Message;
import com.example.mywebquizengine.Model.Chat.MessageStatus;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.UserRepository;
import com.example.mywebquizengine.Service.MessageService;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;


@Validated
@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserRepository userRepository;


    @GetMapping(path = "/chat")
    public String chat(Model model, Authentication authentication) {
        User user = userService.getAuthUser(authentication);
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogs(user.getUsername()));
        model.addAttribute("userList", userService.getUserList());
        return "chat";
    }

    @GetMapping(path = "/chat/{username}")
    public String chatWithUser(Model model, @PathVariable String username, Authentication authentication) {

        /*try {
            User user = userService.reloadUser(username);
        } catch (UsernameNotFoundException e) {
            Group group = messageService.
        }*/
        User user = userService.getAuthUser(authentication);
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogs(user.getUsername()));

        if (userRepository.findById(username).isPresent()) {

            model.addAttribute("user", userService.reloadUser(username));
            model.addAttribute("messages", messageService.
                    getMessages(user.getUsername(), username));

        } else {
            model.addAttribute("messages", messageService.
                    getMessagesInGroup(username));
            model.addAttribute("group", username);
        }





        model.addAttribute("userList", userService.getUserList());
        return "chat";
    }

    @PostMapping(path = "/createGroup")
    @ResponseBody
    public Integer createGroup(@RequestBody Group group) {
        group.setCreator(userService.getAuthUser(SecurityContextHolder.getContext().getAuthentication()));
        group.setImage("default");
        messageService.saveGroup(group);
        return group.getGroup_id();
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
