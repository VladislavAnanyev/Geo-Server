package com.example.mywebquizengine.Controller;


import com.example.mywebquizengine.Model.Chat.Dialog;
import com.example.mywebquizengine.Model.Chat.Message;
import com.example.mywebquizengine.Model.Chat.MessageStatus;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.RabbitMqConfiguration;
import com.example.mywebquizengine.Repos.DialogRepository;
import com.example.mywebquizengine.Repos.MessageRepository;
import com.example.mywebquizengine.Repos.UserRepository;
import com.example.mywebquizengine.Service.MessageService;
import com.example.mywebquizengine.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;



@Controller
@EnableRabbit
@Component
public class ChatController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private MessageRepository messageRepository;

    Logger logger = LoggerFactory.getLogger("main");




    @GetMapping(path = "/chat")
    public String chat(Model model, Authentication authentication) {
        User user = userService.getAuthUser(authentication);
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogs(user.getUsername()));
        model.addAttribute("userList", userService.getUserList());
        return "chat";
    }

    @PostMapping(path = "/checkdialog")
    @ResponseBody
    @PreAuthorize(value = "!@userService.getAuthUser(authentication).username.equals(#user.username)")
    public Long checkDialog(@RequestBody User user) {

        Long dialog_id = messageService.checkDialog(user);

        if (dialog_id == null) {
            Dialog dialog = new Dialog();
          //  Set<User> users = new HashSet<>();
            dialog.addUser(userService.loadUserByUsername(user.getUsername()));
            dialog.addUser(userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication()));
//            users.add(userService.loadUserByUsername(user.getUsername()));
//            users.add(userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication()));
            //dialog.setUsers(users);
            dialogRepository.save(dialog);
            return dialog.getDialogId();
        } else {
            return dialog_id;
        }
    }

    @GetMapping(path = "/chat/{dialog_id}")
    public String chatWithUser(Model model, @PathVariable String dialog_id, Authentication authentication) {

        if (dialogRepository.findDialogByDialogId(Long.valueOf(dialog_id)).getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication()).getUsername()))) {

            Dialog dialog = dialogRepository.findById(Long.valueOf(dialog_id)).get();





            User user = userService.getAuthUser(authentication);
            model.addAttribute("myUsername", user);
            model.addAttribute("lastDialogs", messageService.getDialogs(user.getUsername()));

            model.addAttribute("dialog", dialog.getDialogId());
            model.addAttribute("messages", dialog.getMessages());

            model.addAttribute("dialogObj", dialog);

            model.addAttribute("userList", userService.getUserList());
            return "chat";
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);


    }

    @PostMapping(path = "/createGroup")
    @ResponseBody
    public Long createGroup(@RequestBody Dialog newDialog) {

        User authUser = new User();

        authUser.setUsername(userService.getAuthUser(SecurityContextHolder.getContext().getAuthentication()).getUsername());

        newDialog.addUser(authUser);

        if (newDialog.getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication()).getUsername()))) {

            Dialog dialog = new Dialog();

            newDialog.getUsers().forEach(user -> dialog.addUser(userService.loadUserByUsername(user.getUsername())));

            /*for (User user: newDialog.getUsers()) {
                dialog.addUser(userService.loadUserByUsername(user.getUsername()));
            }*/
            //dialog.addUser(userService.getAuthUser(SecurityContextHolder.getContext().getAuthentication()));

        /*if (newDialog.getName() == null || newDialog.getName().trim().equals("")) {
            StringBuilder name = new StringBuilder();
            for (User user : dialog.getUsers()) {
                name.append(user.getUsername()).append(" ");
            }
            dialog.setName(name.toString());

        } else {
            dialog.setName(newDialog.getName());
        }*/


            if (newDialog.getName() == null) {
                dialog.setName("Конференция");
            } else {
                dialog.setName(newDialog.getName());
            }
            //group.setCreator(userService.getAuthUser(SecurityContextHolder.getContext().getAuthentication()));
            dialog.setImage("default");
            dialogRepository.save(dialog);
            return dialog.getDialogId();

        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);


    }


    @Modifying
    @Transactional
    @MessageMapping("/user/{dialogId}")
    @SendTo("/topic/{dialogId}")
    //@PreAuthorize(value = "@message.sender.username.equals(@userService.getAuthUser(authentication).username)")
    public void sendMessage(@Payload Message message, Authentication authentication) {

        if (message.getSender().getUsername().equals(userService.getAuthUser(authentication).getUsername())) {

            User sender = userService.loadUserByUsername(message.getSender().getUsername());

            // Persistence Bag. Используется костыль
            // для корректного отображения (тесты не инициализируются автоматически)
            //.setTests(new ArrayList<>());

            message.setSender(sender);

            //User recipient = userService.loadUserByUsername(message.getRecipient().getUsername());
            //recipient.setTests(new ArrayList<>());
            //message.setRecipient(recipient);

            message.setDialog(dialogRepository.findById(message.getDialog().getDialogId()).get());


            // Устанавливается часовой пояс для хранения времени в БД постоянно по Москве
            // В БД будет сохраняться Московское время независимо от местоположения сервера/пользователя
            TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
            Calendar nowDate = new GregorianCalendar();
            nowDate.setTimeZone(timeZone);
            message.setTimestamp(nowDate);;

            message.setStatus(MessageStatus.DELIVERED);
            messageService.saveMessage(message);

            Dialog dialog = dialogRepository.findById(message.getDialog().getDialogId()).get();

            User authUser = userService.getAuthUserNoProxy(authentication);

            for (User user :dialog.getUsers()) {
                if (!user.getUsername().equals(authUser.getUsername())) {

                    rabbitTemplate.setExchange("message-exchange");

                    simpMessagingTemplate.convertAndSend("/topic/" + user.getUsername(),
                            messageRepository.findMessageById(message.getId()));

                    rabbitTemplate.convertAndSend(
                            "android", messageRepository.findMessageById(message.getId()));

                }
            }

            //simpMessagingTemplate.convertAndSend("/topic/application", message);

            //return messageRepository.getMessageById(message.getId());

        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);



    }


    @RabbitListener(queues = "WebMessageQueue")
    public void getWebMessageFromRabbitMq(Message message) {
        logger.info("123");
        System.out.println("Сообщение получено на веб, " + message.toString());
        //sendMessage(message, SecurityContextHolder.getContext().getAuthentication());
    }

    /*@RabbitListener(queues = "AndroidMessageQueue")
    public void getAndroidMessageFromRabbitMq(Message message) {
        logger.info("123");
        System.out.println("Сообщение получено на андройд, " + message.toString());
        //sendMessage(message, SecurityContextHolder.getContext().getAuthentication());
    }*/

    @GetMapping(path = "/error")
    public String handleError(){
        return "error";
    }



}
