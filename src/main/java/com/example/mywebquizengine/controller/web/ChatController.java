package com.example.mywebquizengine.controller.web;


import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.model.chat.Dialog;
import com.example.mywebquizengine.model.projection.DialogView;
import com.example.mywebquizengine.service.MessageService;
import com.example.mywebquizengine.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.security.Principal;


@Controller
@Validated
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;


    @GetMapping(path = "/chat")
    public String chat(Model model, @AuthenticationPrincipal Principal principal) {

        User user = userService.loadUserByUsernameProxy(principal.getName());
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogsForApi(principal.getName()));
        model.addAttribute("userList", userService.findMyFriends(principal.getName()));
        return "chat";
    }

    @PostMapping(path = "/checkdialog")
    @ResponseBody
    @PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public Long checkDialog(@RequestBody User user, @AuthenticationPrincipal Principal principal) {

        return messageService.checkDialog(user.getUsername(), principal.getName());

    }

    @GetMapping(path = "/chat/{dialog_id}")
    @Transactional
    public String chatWithUser(Model model, @PathVariable String dialog_id,
                               @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
                               @RequestParam(required = false, defaultValue = "50") @Min(1) @Max(100) Integer pageSize,
                               @RequestParam(defaultValue = "timestamp") String sortBy,
                               @AuthenticationPrincipal Principal principal) {

        //DialogView dialog = messageService.getDialogWithPaging(dialog_id, page, pageSize, sortBy);
        DialogView dialog = messageService.getMessages(Long.valueOf(dialog_id), page, pageSize, sortBy, principal.getName());
        if (dialog.getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(principal.getName()))) {

            model.addAttribute("lastDialogs", messageService.getDialogsForApi(principal.getName()));
            model.addAttribute("dialog", dialog.getDialogId());
            model.addAttribute("messages", dialog.getMessages());
            model.addAttribute("dialogObj", dialog);
            model.addAttribute("userList", userService.findMyFriends(principal.getName()));

            return "chat";

        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);


    }

    @GetMapping(path = "/chat/nextPages")
    @Transactional
    @ResponseBody
    public DialogView chatWithUserPages(@RequestParam String dialog_id,
                                        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
                                        @RequestParam(required = false, defaultValue = "50") @Min(1) @Max(100) Integer pageSize,
                                        @RequestParam(defaultValue = "timestamp") String sortBy,
                                        @AuthenticationPrincipal Principal principal) {
        return messageService.getMessages(Long.valueOf(dialog_id), page, pageSize, sortBy, principal.getName());
    }

    @PostMapping(path = "/createGroup")
    @ResponseBody
    public Long createGroup(@Valid @RequestBody Dialog newDialog,
                            @AuthenticationPrincipal Principal principal
    ) throws JsonProcessingException, ParseException {
        return messageService.createGroup(newDialog, principal.getName());
    }


    @GetMapping(path = "/error")
    public String handleError() {
        return "error";
    }


}
