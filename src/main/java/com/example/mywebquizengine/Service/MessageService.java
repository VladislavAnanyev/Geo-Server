package com.example.mywebquizengine.Service;


import com.example.mywebquizengine.Model.Chat.Dialog;
import com.example.mywebquizengine.Model.Chat.Message;

//import com.example.mywebquizengine.Model.Projection.MessageForStompView;
import com.example.mywebquizengine.Model.Chat.MessageStatus;
import com.example.mywebquizengine.Model.Projection.Api.MessageForApiViewCustomQuery;
import com.example.mywebquizengine.Model.Projection.DialogWithUsersViewPaging;
import com.example.mywebquizengine.Model.User;

import com.example.mywebquizengine.Repos.DialogRepository;
import com.example.mywebquizengine.Repos.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private UserService userService;




/*    public void saveGroup(Group group) {
        groupRepository.save(group);
    }*/

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public Dialog tryToSaveDialog(Dialog dialog) {
        if (!dialogRepository.findById(dialog.getDialogId()).isPresent()) {
            dialogRepository.save(dialog);
        }
        return dialogRepository.findById(dialog.getDialogId()).get();

    }

    public Long checkDialog(String username, String authUsername) {

        //User authUser = userService.loadUserByUsername(authUsername);

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

    public Long checkDialogForApi(String username1, String username2) {
        return dialogRepository.findDialogByName(username1, username2);
    }

    public void checkDialog(Dialog dialog) {
        if (!dialogRepository.findById(dialog.getDialogId()).isPresent()) {
            dialogRepository.save(dialog);
        }
    }

    public List<Message> getDialogs(String username) {
        return messageRepository.getDialogs(username);
    }

    public ArrayList<MessageForApiViewCustomQuery> getDialogsForApi(String username) {

        List<MessageForApiViewCustomQuery> messageViews = messageRepository.getDialogsForApi(username);

        /*ArrayList<MessageForApiView> messageForStompViews = new ArrayList<>();

        for (int i = 0; i < messageViews.size(); i++) {
            messageForStompViews.add(messageRepository.findMessageById(messageViews.get(i)));
        }*/

        //return messageRepository.findAllById((ArrayList<Integer>) messageViews);
        //return messageForStompViews;
        return (ArrayList<MessageForApiViewCustomQuery>) messageViews;
    }

    public void deleteMessage(Long id) {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setStatus(MessageStatus.DELETED);
            messageRepository.save(message);
        }
    }

    @Transactional
    public DialogWithUsersViewPaging getMessages(Long dialogId, Integer page, Integer pageSize, String sortBy, Principal principal) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by(sortBy).descending());
        dialogRepository.findById(dialogId).get().setPaging(paging);
        DialogWithUsersViewPaging dialog = dialogRepository.findAllDialogByDialogId(dialogId);

        // If user contains in dialog
        if (dialog.getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(principal.getName()))) {

            dialogRepository.findById(dialogId).get().setPaging(paging);

            return dialogRepository.findAllDialogByDialogId(dialogId);
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);

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
