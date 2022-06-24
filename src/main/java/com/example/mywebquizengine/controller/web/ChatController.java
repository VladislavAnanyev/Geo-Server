package com.example.mywebquizengine.controller.web;


import com.example.mywebquizengine.model.chat.dto.output.DialogView;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.model.CreateGroupModelMapper;
import com.example.mywebquizengine.service.chat.MessageService;
import com.example.mywebquizengine.service.user.UserService;
import com.example.mywebquizengine.service.utils.RabbitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Controller
@Validated
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @GetMapping(path = "/chat")
    public String chat(Model model, @AuthenticationPrincipal User authUser) {

        model.addAttribute("myUsername", authUser.getUserId());
        model.addAttribute("lastDialogs", messageService.getDialogs(authUser.getUserId()));
        model.addAttribute("userList", userService.findMyFriends(authUser.getUserId()));
        return "chat";
    }

    @GetMapping(path = "/exchange")
    @ResponseBody
    public String getExchangeName(@AuthenticationPrincipal User authUser) {
        return RabbitUtil.getExchangeName(authUser.getUserId());
    }

    @PostMapping(path = "/checkdialog")
    @ResponseBody
    @PreAuthorize(value = "!#authUser.userId.equals(#user.userId)")
    public Long checkDialog(@RequestBody User user, @AuthenticationPrincipal User authUser) {
        return messageService.createDialog(user.getUserId(), authUser.getUserId());
    }

    @GetMapping(path = "/chat/{dialog_id}")
    @Transactional
    public String chatWithUser(Model model, @PathVariable String dialog_id,
                               @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
                               @RequestParam(required = false, defaultValue = "50") @Min(1) @Max(100) Integer pageSize,
                               @RequestParam(defaultValue = "timestamp") String sortBy,
                               @AuthenticationPrincipal User authUser) {

        DialogView dialog = messageService.getMessages(Long.valueOf(dialog_id), page, pageSize, sortBy, authUser.getUserId());

        model.addAttribute("lastDialogs", messageService.getDialogs(authUser.getUserId()));
        model.addAttribute("dialog", dialog.getDialogId());
        model.addAttribute("messages", dialog.getMessages());
        model.addAttribute("dialogObj", dialog);
        model.addAttribute("userList", userService.findMyFriends(authUser.getUserId()));
        return "chat";
    }

    @GetMapping(path = "/chat/nextPages")
    @Transactional
    @ResponseBody
    public DialogView chatWithUserPages(@RequestParam String dialog_id,
                                        @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
                                        @RequestParam(required = false, defaultValue = "50") @Min(1) @Max(100) Integer pageSize,
                                        @RequestParam(defaultValue = "timestamp") String sortBy,
                                        @AuthenticationPrincipal User authUser) {
        return messageService.getMessages(Long.valueOf(dialog_id), page, pageSize, sortBy, authUser.getUserId());
    }

    @PostMapping(path = "/createGroup")
    @ResponseBody
    public Long createGroup(@Valid @RequestBody CreateGroupRequest request, @AuthenticationPrincipal User authUser) {
        return messageService.createGroup(CreateGroupModelMapper.map(request, authUser.getUserId()));
    }

    @GetMapping(path = "/error")
    public String handleError() {
        return "error";
    }


}
