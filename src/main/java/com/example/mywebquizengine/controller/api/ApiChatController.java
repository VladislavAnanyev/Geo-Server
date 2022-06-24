package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.chat.dto.input.EditMessageRequest;
import com.example.mywebquizengine.model.chat.dto.output.DialogView;
import com.example.mywebquizengine.model.chat.dto.output.LastDialog;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.chat.MessageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ApiChatController {

    private final MessageService messageService;

    public ApiChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    @DeleteMapping(path = "/message/{id}")
    public void deleteMessage(@PathVariable Long id,
                              @ApiIgnore @AuthenticationPrincipal User authUser) {
        messageService.deleteMessage(id, authUser.getUserId());
    }

    @PutMapping(path = "/message/{id}")
    public void editMessage(@PathVariable Long id, @RequestBody EditMessageRequest editMessageRequest,
                            @ApiIgnore @AuthenticationPrincipal User authUser) {
        messageService.editMessage(id, editMessageRequest.getContent(), authUser.getUserId());
    }

    @GetMapping(path = "/dialog/{dialogId}")
    public DialogView getMessages(@PathVariable Long dialogId,
                                  @RequestParam(required = false, defaultValue = "0") Integer page,
                                  @RequestParam(required = false, defaultValue = "50") Integer pageSize,
                                  @RequestParam(defaultValue = "timestamp") String sortBy,
                                  @ApiIgnore @AuthenticationPrincipal User authUser) {
        return messageService.getMessages(dialogId, page, pageSize, sortBy, authUser.getUserId());
    }

    @GetMapping(path = "/dialogs")
    public List<LastDialog> getDialogs(@ApiIgnore @AuthenticationPrincipal User authUser) {
        return messageService.getDialogs(authUser.getUserId());
    }

    @PostMapping(path = "/dialog/create")
    public Long checkDialog(@RequestParam Long userId, @ApiIgnore @AuthenticationPrincipal User authUser) {
        return messageService.createDialog(userId, authUser.getUserId());
    }

}
