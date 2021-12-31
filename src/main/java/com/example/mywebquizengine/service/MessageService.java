package com.example.mywebquizengine.service;


import com.example.mywebquizengine.model.chat.Dialog;
import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.model.chat.MessageStatus;
import com.example.mywebquizengine.model.projection.DialogView;
import com.example.mywebquizengine.model.projection.LastDialog;
import com.example.mywebquizengine.model.projection.MessageView;
import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.model.projection.TypingView;
import com.example.mywebquizengine.repos.DialogRepository;
import com.example.mywebquizengine.repos.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    SessionRegistry sessionRegistry;
    @Autowired
    private DialogRepository dialogRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${hostname}")
    private String hostname;


    public void saveMessage(Message message) {
        messageRepository.save(message);
    }


    public Long checkDialog(String username, String authUsername) {

        if (!authUsername.equals(username)) {
            Long dialog_id = dialogRepository.findDialogByName(username,
                    authUsername);

            if (dialog_id == null) {
                Dialog dialog = new Dialog();

                dialog.addUser(userService.loadUserByUsername(username));
                dialog.addUser(userService.loadUserByUsername(authUsername));

                dialogRepository.save(dialog);
                return dialog.getDialogId();
            } else {
                return dialog_id;
            }
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);


    }

    public String getCompanion(String name, Set<User> users) {


        if (users.size() > 2) {
            return name;
        } else if (users.size() == 2) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            users.removeIf(user -> user.getUsername().equals(username));
            return users.iterator().next().getUsername();
        } else if (users.size() == 1) {
            return users.iterator().next().getUsername();
        } else {
            return "Пустой диалог";
        }

    }

    public String getCompanionAvatar(String image, Set<User> users) {

        if (users.size() > 2) {
            return image;
        } else if (users.size() == 2) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            users.removeIf(user -> user.getUsername().equals(username));
            return users.iterator().next().getPhotos().get(0).getUrl();
        } else if (users.size() == 1) {
            return users.iterator().next().getPhotos().get(0).getUrl();
        } else {
            return "Пустой диалог";
        }

    }

    public String getCompanionForLastDialogs(Long dialog_id) {
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialog_id);
        if (optionalDialog.isPresent()) {
            Dialog dialog = optionalDialog.get();
            Set<User> users = dialog.getUsers();

            if (users.size() > 2) {
                return dialog.getName();
            } else if (users.size() == 2) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                users.removeIf(user -> user.getUsername().equals(username));
                return users.iterator().next().getUsername();
            } else if (users.size() == 1) {
                return users.iterator().next().getUsername();
            } else {
                return "Пустой диалог";
            }
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    public String getCompanionAvatarForLastDialogs(Long dialog_id) {
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialog_id);
        if (optionalDialog.isPresent()) {
            Dialog dialog = optionalDialog.get();
            Set<User> users = dialog.getUsers();

            if (users.size() > 2) {
                return dialog.getImage();
            } else if (users.size() == 2) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                users.removeIf(user -> user.getUsername().equals(username));
                return users.iterator().next().getPhotos().get(0).getUrl();
            } else if (users.size() == 1) {
                return users.iterator().next().getPhotos().get(0).getUrl();
            } else {
                return "Пустой диалог";
            }
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    public ArrayList<LastDialog> getDialogsForApi(String username) {

        List<LastDialog> messageViews = messageRepository.getDialogsForApi(username);

        return (ArrayList<LastDialog>) messageViews;
    }


    @Transactional
    public void deleteMessage(Long id, String username) throws JsonProcessingException, ParseException {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            if (message.getSender().getUsername().equals(username)) {
                message.setStatus(MessageStatus.DELETED);

                ProjectionFactory pf = new SpelAwareProxyProjectionFactory();
                MessageView messageDto = pf.createProjection(MessageView.class, message);

                JSONObject jsonObject = (JSONObject) JSONValue
                        .parseWithException(objectMapper.writeValueAsString(messageDto));
                jsonObject.put("type", "DELETE-MESSAGE");

                for (User user : message.getDialog().getUsers()) {

                    rabbitTemplate.convertAndSend(user.getUsername(),
                            jsonObject);

                }


            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public DialogView getMessages(Long dialogId,
                                  Integer page,
                                  Integer pageSize,
                                  String sortBy,
                                  String username) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        Optional<Dialog> optionalDialog = dialogRepository.findById(dialogId);
        optionalDialog.ifPresent(dialog -> dialog.setPaging(paging));
        DialogView dialog = dialogRepository.findAllDialogByDialogId(dialogId);

        // If user contains in dialog
        if (dialog.getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(username))) {
            return dialog;
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);

    }

    @Transactional
    public void editMessage(Message newMessage, String username) throws JsonProcessingException, ParseException {
        Optional<Message> optionalMessage = messageRepository.findById(newMessage.getId());
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            if (message.getSender().getUsername().equals(username)) {
                message.setContent(newMessage.getContent());
                message.setStatus(MessageStatus.EDIT);

                ProjectionFactory pf = new SpelAwareProxyProjectionFactory();
                MessageView messageDto = pf.createProjection(MessageView.class, message);

                JSONObject jsonObject = (JSONObject) JSONValue
                        .parseWithException(objectMapper.writeValueAsString(messageDto));
                jsonObject.put("type", "EDIT-MESSAGE");

                for (User user : message.getDialog().getUsers()) {

                    rabbitTemplate.convertAndSend(user.getUsername(),
                            jsonObject);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
    }


    @Transactional
    public void sendMessage(Message message) throws JsonProcessingException, ParseException {
        /*Dialog dialog = dialogRepository.findById(message.getDialog().getDialogId()).get();
        User sender = userService.loadUserByUsernameProxy(message.getSender().getUsername());
        message.setSender(sender);
        message.setDialog(dialog);

        JSONObject jsonObject;

        if (message.getType().equals("MESSAGE")) {
            message.setTimestamp(new Date());
            message.setStatus(MessageStatus.DELIVERED);

            messageRepository.save(message);

            ProjectionFactory pf = new SpelAwareProxyProjectionFactory();
            MessageView messageView = pf.createProjection(MessageView.class, message);
            jsonObject = (JSONObject) JSONValue.parseWithException(objectMapper.writeValueAsString(messageView));
        } else if (message.getType().equals("TYPING")) {
            ProjectionFactory pf = new SpelAwareProxyProjectionFactory();
            TypingView typingView = pf.createProjection(TypingView.class, message);
            jsonObject = (JSONObject) JSONValue.parseWithException(objectMapper.writeValueAsString(typingView));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        for (User user : dialog.getUsers()) {
            simpMessagingTemplate.convertAndSend("/topic/" + user.getUsername(), jsonObject);
            rabbitTemplate.convertAndSend(user.getUsername(), "", jsonObject);
        }*/

        User sender = userService.loadUserByUsernameProxy(message.getSender().getUsername());

        message.setSender(sender);
        message.setDialog(dialogRepository.findById(message.getDialog().getDialogId()).get());
        message.setTimestamp(new Date());
        message.setStatus(MessageStatus.DELIVERED);

        messageRepository.save(message);

        Dialog dialog = dialogRepository.findById(message.getDialog().getDialogId()).get();

        ProjectionFactory pf = new SpelAwareProxyProjectionFactory();
        MessageView messageDto = pf.createProjection(MessageView.class, message);

        JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(objectMapper.writeValueAsString(messageDto));
        jsonObject.put("type", "MESSAGE");

        for (User user : dialog.getUsers()) {
            rabbitTemplate.convertAndSend(user.getUsername(), "", jsonObject);
        }



    }


    public Long createGroup(Dialog newDialog, String username) throws JsonProcessingException, ParseException {

        User authUser = new User();

        authUser.setUsername(username);

        newDialog.addUser(authUser);

        if (newDialog.getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(username))) {

            Dialog dialog = new Dialog();

            newDialog.getUsers().forEach(user -> dialog.addUser(userService.loadUserByUsername(user.getUsername())));

            Message message = new Message();
            message.setContent("Группа создана");
            message.setSender(userService.loadUserByUsername(username));
            message.setStatus(MessageStatus.DELIVERED);
            message.setTimestamp(new Date());
            message.setDialog(dialog);

            dialog.setMessages(new ArrayList<>());
            dialog.getMessages().add(message);


            if (newDialog.getName() == null || newDialog.getName().equals("")) {
                dialog.setName("Конференция");
            } else {
                dialog.setName(newDialog.getName());
            }

            //group.setCreator(userService.getAuthUser(SecurityContextHolder.getContext().getAuthentication()));
            dialog.setImage("https://" + hostname + "/img/default.jpg");
            dialogRepository.save(dialog);

            ProjectionFactory pf = new SpelAwareProxyProjectionFactory();
            MessageView messageDto = pf.createProjection(MessageView.class, message);

            JSONObject jsonObject = (JSONObject) JSONValue
                    .parseWithException(objectMapper.writeValueAsString(messageDto));
            jsonObject.put("type", "MESSAGE");
            for (User user : dialog.getUsers()) {
/*                simpMessagingTemplate.convertAndSend("/topic/" + user.getUsername(),
                        jsonObject);*/
            }


            return dialog.getDialogId();

        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    public DialogView getDialogWithPaging(String dialog_id, Integer page, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());

        dialogRepository.findById(Long.valueOf(dialog_id)).get().setPaging(paging);
        return dialogRepository.findAllDialogByDialogId(Long.valueOf(dialog_id));
    }
}