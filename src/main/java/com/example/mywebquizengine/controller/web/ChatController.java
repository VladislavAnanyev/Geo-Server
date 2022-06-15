package com.example.mywebquizengine.controller.web;


import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.model.chat.Dialog;
import com.example.mywebquizengine.model.projection.DialogView;
import com.example.mywebquizengine.service.MessageService;
import com.example.mywebquizengine.service.UserService;
import com.example.mywebquizengine.service.utils.RabbitUtil;
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
import java.security.NoSuchAlgorithmException;
import java.security.Principal;


@Controller
@Validated
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;


    @GetMapping(path = "/chat")
    public String chat(Model model, @AuthenticationPrincipal User authUser) {

        User user = userService.loadUserByUsernameProxy(authUser.getUsername());
        model.addAttribute("myUsername", user);
        model.addAttribute("lastDialogs", messageService.getDialogsForApi(authUser.getUsername()));
        model.addAttribute("userList", userService.findMyFriends(authUser.getUsername()));
        return "chat";
    }

    @GetMapping(path = "/exchange")
    @ResponseBody
    public String getExchangeName(@AuthenticationPrincipal User authUser) {
        return RabbitUtil.getExchangeName(authUser.getUsername());
    }

    @PostMapping(path = "/checkdialog")
    @ResponseBody
    @PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public Long checkDialog(@RequestBody User user, @AuthenticationPrincipal User authUser) {

        return messageService.createDialog(user.getUsername(), authUser.getUsername());

    }

    @GetMapping(path = "/chat/{dialog_id}")
    @Transactional
    public String chatWithUser(Model model, @PathVariable String dialog_id,
                               @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
                               @RequestParam(required = false, defaultValue = "50") @Min(1) @Max(100) Integer pageSize,
                               @RequestParam(defaultValue = "timestamp") String sortBy,
                               @AuthenticationPrincipal User authUser) {

        //DialogView dialog = messageService.getDialogWithPaging(dialog_id, page, pageSize, sortBy);
        DialogView dialog = messageService.getMessages(Long.valueOf(dialog_id), page, pageSize, sortBy, authUser.getUsername());
        if (dialog.getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(authUser.getUsername()))) {

            model.addAttribute("lastDialogs", messageService.getDialogsForApi(authUser.getUsername()));
            model.addAttribute("dialog", dialog.getDialogId());
            model.addAttribute("messages", dialog.getMessages());
            model.addAttribute("dialogObj", dialog);
            model.addAttribute("userList", userService.findMyFriends(authUser.getUsername()));

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
                                        @AuthenticationPrincipal User authUser) {
        return messageService.getMessages(Long.valueOf(dialog_id), page, pageSize, sortBy, authUser.getUsername());
    }

    @PostMapping(path = "/createGroup")
    @ResponseBody
    public Long createGroup(@Valid @RequestBody Dialog newDialog,
                            @AuthenticationPrincipal User authUser
    ) throws JsonProcessingException, ParseException, NoSuchAlgorithmException {
        return messageService.createGroup(newDialog, authUser.getUsername());
    }


    @GetMapping(path = "/error")
    public String handleError() {
        return "error";
    }


}
