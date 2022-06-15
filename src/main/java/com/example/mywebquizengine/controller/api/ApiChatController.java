package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.chat.EditMessageRequest;
import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.model.projection.LastDialog;
import com.example.mywebquizengine.model.projection.DialogView;
import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;

@RestController
@RequestMapping(path = "/api")
public class ApiChatController {

    @Autowired
    private MessageService messageService;

    @DeleteMapping(path = "/message/{id}")
    public void deleteMessage(@PathVariable Long id,
                              @ApiIgnore @AuthenticationPrincipal User authUser
    ) throws JsonProcessingException {
        messageService.deleteMessage(id, authUser.getUsername());
    }

    @PutMapping(path = "/message/{id}")
    public void editMessage(@PathVariable Long id,
                            @RequestBody EditMessageRequest editMessageRequest,
                            @ApiIgnore @AuthenticationPrincipal User authUser
    ) throws JsonProcessingException {

        Message message = new Message();
        message.setContent(editMessageRequest.getContent());
        message.setId(id);

        messageService.editMessage(message, authUser.getUsername());
    }

    @GetMapping(path = "/dialog/{dialogId}")
    public DialogView getMessages(@PathVariable Long dialogId,
                                  @RequestParam(required = false,defaultValue = "0") Integer page,
                                  @RequestParam(required = false,defaultValue = "50") Integer pageSize,
                                  @RequestParam(defaultValue = "timestamp") String sortBy,
                                  @ApiIgnore @AuthenticationPrincipal User authUser) {
        return messageService.getMessages(dialogId, page, pageSize, sortBy, authUser.getUsername());
    }

    @GetMapping(path = "/dialogs")
    public ArrayList<LastDialog> getDialogs(@ApiIgnore @AuthenticationPrincipal User authUser) {
        return messageService.getDialogsForApi(authUser.getUsername());
    }

    @PostMapping(path = "/dialog/create")
    public Long checkDialog(@RequestParam String username, @ApiIgnore @AuthenticationPrincipal User authUser) {
        return messageService.createDialog(username, authUser.getUsername());
    }

}
