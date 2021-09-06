package com.example.mywebquizengine.Service;


import com.example.mywebquizengine.Model.Chat.Dialog;
import com.example.mywebquizengine.Model.Chat.Message;
import com.example.mywebquizengine.Model.User;

import com.example.mywebquizengine.Repos.DialogRepository;
import com.example.mywebquizengine.Repos.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    MessageRepository messageRepository;


    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private UserService userService;





    public ArrayList<Message> getMessages(String sent, String received) {
        return (ArrayList<Message>) messageRepository.getMessagesByUsername(sent, received);
    }

    public ArrayList<Message> getMessagesInGroup(String group) {
        return (ArrayList<Message>) messageRepository.getMessagesByGroup(group);
    }

/*    public void saveGroup(Group group) {
        groupRepository.save(group);
    }*/

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public Dialog tryToSaveDialog(Dialog dialog) {
        if (!dialogRepository.findById(dialog.getDialog_id()).isPresent()) {
            dialogRepository.save(dialog);
        }
        return dialogRepository.findById(dialog.getDialog_id()).get();

    }

    public Long checkDialog(User user) {
        return dialogRepository.findDialogByName(user.getUsername(),
                userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication()).getUsername());
    }

    public void checkDialog(Dialog dialog) {
        if (!dialogRepository.findById(dialog.getDialog_id()).isPresent()) {
            dialogRepository.save(dialog);
        }
    }

    public List<Message> getDialogs(String username) {
        return messageRepository.getDialogs(username);
    }

    /*public List<Message> getDialogs(String username) {
       List<String> lastTime = messageRepository.getTimeLastMessagesByUsername(username);
       List<Message> dialogs = new ArrayList<>();
       List<Message> dialogsTemp = new ArrayList<>();
       List<String> users = new ArrayList<>();

        for (int i = 0; i < lastTime.size(); i++) {
            dialogsTemp.add(messageRepository.getDialogsByTimestamp(lastTime.get(i)));
        }

        for (int i = 0; i < lastTime.size(); i++) {
            //dialogs.add(messageRepository.getDialogsByTimestamp(lastTime.get(i)));
            users.add(dialogsTemp.get(i).getSender().getUsername());
            users.add(dialogsTemp.get(i).getRecipient().getUsername());
        }

        users = users.stream().distinct().collect(Collectors.toList());
        //users.removeIf(user -> user.equals(userService.getThisUser().getUsername()));

        String senderUsername = "";
        String recipientUsername = "";

        for (int i = 0; i < lastTime.size(); i++) {

            senderUsername = messageRepository
                    .getDialogsByTimestamp(lastTime.get(i))
                    .getSender()
                    .getUsername();

            recipientUsername = messageRepository
                    .getDialogsByTimestamp(lastTime.get(i))
                    .getRecipient()
                    .getUsername();

            if (users.contains(senderUsername) || users.contains(recipientUsername)) {
                dialogs.add(messageRepository.getDialogsByTimestamp(lastTime.get(i)));

                users.remove(recipientUsername); // if exist
                users.remove(senderUsername); // if exist
            }
        }


        return dialogs;
    }*/
}
