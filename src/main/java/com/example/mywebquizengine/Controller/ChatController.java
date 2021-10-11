package com.example.mywebquizengine.Controller;


import com.example.mywebquizengine.Model.Chat.Dialog;
import com.example.mywebquizengine.Model.Chat.Message;
import com.example.mywebquizengine.Model.Chat.MessageStatus;
import com.example.mywebquizengine.Model.Projection.Api.MessageForApiView;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.DialogRepository;
import com.example.mywebquizengine.Repos.MessageRepository;
import com.example.mywebquizengine.Service.MessageService;
import com.example.mywebquizengine.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Hibernate;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;



@Controller
@EnableRabbit
@Component
@Validated
public class ChatController {

    @Value("${hostname}")
    private String hostname;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    SessionRegistry sessionRegistry;

    Logger logger = LoggerFactory.getLogger("main");


    @Autowired
    private ObjectMapper objectMapper;



    @GetMapping(path = "/chat")
    public String chat(Model model, @AuthenticationPrincipal Principal principal) {

        User user = userService.loadUserByUsernameProxy(principal.getName());
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogs(user.getUsername()));
        model.addAttribute("userList", userService.getUserList());
        return "chat";
    }

    @PostMapping(path = "/checkdialog")
    @ResponseBody
    @PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public Long checkDialog(@RequestBody User user, @AuthenticationPrincipal Principal principal) {

        String s = principal.getName();
        Long dialog_id = messageService.checkDialog(user, principal.getName());

        if (dialog_id == null) {
            Dialog dialog = new Dialog();
          //  Set<User> users = new HashSet<>();
            dialog.addUser(userService.loadUserByUsername(user.getUsername()));
            dialog.addUser(userService.loadUserByUsername(principal.getName()));
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
    public String chatWithUser(Model model, @PathVariable String dialog_id, @AuthenticationPrincipal Principal principal) {

        if (dialogRepository.findDialogByDialogId(Long.valueOf(dialog_id)).getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(principal.getName()))) {

            Dialog dialog = dialogRepository.findById(Long.valueOf(dialog_id)).get();





            User user = userService.loadUserByUsername(principal.getName());
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
    public Long createGroup(@RequestBody Dialog newDialog, @AuthenticationPrincipal Principal principal) {

        User authUser = new User();

        authUser.setUsername(principal.getName());

        newDialog.addUser(authUser);

        if (newDialog.getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(principal.getName()))) {

            Dialog dialog = new Dialog();

            newDialog.getUsers().forEach(user -> dialog.addUser(userService.loadUserByUsername(user.getUsername())));


            if (newDialog.getName() == null) {
                dialog.setName("Конференция");
            } else {
                dialog.setName(newDialog.getName());
            }
            //group.setCreator(userService.getAuthUser(SecurityContextHolder.getContext().getAuthentication()));
            dialog.setImage("https://" + hostname + "/img/default.jpg");
            dialogRepository.save(dialog);
            return dialog.getDialogId();

        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);


    }


    @Modifying
    @Transactional
    @MessageMapping("/user/{dialogId}")
    //@SendTo("/topic/{dialogId}")
    //@PreAuthorize(value = "@message.sender.username.equals(@userService.getAuthUser(authentication).username)")
    public void sendMessage(@Valid @Payload Message message, @AuthenticationPrincipal Principal principal) throws JsonProcessingException, ParseException {

        String s = principal.getName();

        User authUser = userService.loadUserByUsernameProxy(s);

        if (message.getSender().getUsername().equals(authUser.getUsername())) {


            message.setSender(authUser);

            message.setDialog(dialogRepository.findById(message.getDialog().getDialogId()).get());

            // Устанавливается часовой пояс для хранения времени в БД постоянно по Москве
            // В БД будет сохраняться Московское время независимо от местоположения сервера/пользователя
            TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
            Calendar nowDate = new GregorianCalendar();
            nowDate.setTimeZone(timeZone);
            message.setTimestamp(nowDate);

            message.setStatus(MessageStatus.DELIVERED);
            messageService.saveMessage(message);

            Dialog dialog = dialogRepository.findById(message.getDialog().getDialogId()).get();
            MessageForApiView messageDto = messageRepository.findMessageById(message.getId());

            for (User user :dialog.getUsers()) {

                rabbitTemplate.setExchange("message-exchange");


                JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(objectMapper.writeValueAsString(messageDto));
                jsonObject.put("type", "MESSAGE");

                rabbitTemplate.convertAndSend(user.getUsername(),
                       jsonObject);

                if (!user.getUsername().equals(authUser.getUsername())) {


                    simpMessagingTemplate.convertAndSend("/topic/" + user.getUsername(),
                            jsonObject);

                }
            }

        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);

    }




    @Modifying
    @Transactional
    @RabbitListener(queues = "incoming-messages")
    public void getMessageFromAndroid(@Valid Message message) throws JsonProcessingException, ParseException {

        try {
            System.out.println("Сообщение получено из андройда");


            User sender = userService.loadUserByUsername(message.getSender().getUsername());

            // Persistence Bag. Используется костыль
            // для корректного отображения (тесты не инициализируются автоматически)
            //.setTests(new ArrayList<>());

            message.setSender(sender);



            message.setDialog(dialogRepository.findById(message.getDialog().getDialogId()).get());


            // Устанавливается часовой пояс для хранения времени в БД постоянно по Москве
            // В БД будет сохраняться Московское время независимо от местоположения сервера/пользователя
            TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
            Calendar nowDate = new GregorianCalendar();
            nowDate.setTimeZone(timeZone);
            message.setTimestamp(nowDate);


            message.setStatus(MessageStatus.DELIVERED);
            messageService.saveMessage(message);

            Dialog dialog = dialogRepository.findById(message.getDialog().getDialogId()).get();


            Hibernate.initialize(dialog.getUsers());
            //User authUser = userService.getAuthUserNoProxy(authentication);

            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(objectMapper.writeValueAsString(messageRepository.findMessageById(message.getId())));
            jsonObject.put("type", "MESSAGE");

            for (User user :dialog.getUsers()) {

                //if (!user.getUsername().equals(authUser.getUsername())) {

                simpMessagingTemplate.convertAndSend("/topic/" + user.getUsername(),
                        jsonObject);

                rabbitTemplate.convertAndSend(user.getUsername(),
                        jsonObject);

                //}
            }
        } catch (Exception e) {
            System.out.println("BAD MESSAGE");
        }


        //}
    }

    /*@RabbitListener(queues = "AndroidMessageQueue")
    public void getAndroidMessageFromRabbitMq(Message message) {
        logger.info("123");
        System.out.println("Сообщение получено на андройд, " + message.toString());
        //sendMessage(message, SecurityContextHolder.getContext().getAuthentication());
    }*/



    @GetMapping(path = "/error")
    public String handleError() {
        return "error";
    }



}
