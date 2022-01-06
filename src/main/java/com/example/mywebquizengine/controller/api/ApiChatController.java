package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.model.projection.LastDialog;
import com.example.mywebquizengine.model.projection.DialogView;
import com.example.mywebquizengine.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;

@RestController
@RequestMapping(path = "/api")
public class ApiChatController {

    @Autowired
    private MessageService messageService;

    @DeleteMapping(path = "/message/{id}")
    public void deleteMessage(@PathVariable Long id,
                              @AuthenticationPrincipal Principal principal
    ) {
        messageService.deleteMessage(id, principal.getName());
    }

    @PutMapping(path = "/message/{id}")
    public void editMessage(@RequestBody Message message,
                            @AuthenticationPrincipal Principal principal
    ) {
        messageService.editMessage(message, principal.getName());
    }

    @GetMapping(path = "/messages")
    public DialogView getMessages(@RequestParam Long dialogId,
                                  @RequestParam(required = false,defaultValue = "0") Integer page,
                                  @RequestParam(required = false,defaultValue = "50") Integer pageSize,
                                  @RequestParam(defaultValue = "timestamp") String sortBy,
                                  @AuthenticationPrincipal Principal principal) {
        return messageService.getMessages(dialogId, page, pageSize, sortBy, principal.getName());
    }

    @GetMapping(path = "/dialogs")
    public ArrayList<LastDialog> getDialogs(@AuthenticationPrincipal Principal principal) {
        return messageService.getDialogsForApi(principal.getName());
    }

    @GetMapping(path = "/getDialogId")
    public Long checkDialog(@RequestParam String username, @AuthenticationPrincipal Principal principal) {
        return messageService.checkDialog(username, principal.getName());
    }


}
