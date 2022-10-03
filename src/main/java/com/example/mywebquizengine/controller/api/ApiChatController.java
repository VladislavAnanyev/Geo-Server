package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.chat.model.dto.input.EditMessageRequest;
import com.example.mywebquizengine.chat.model.dto.output.DialogView;
import com.example.mywebquizengine.chat.model.dto.output.LastDialog;
import com.example.mywebquizengine.chat.model.FileResponse;
import com.example.mywebquizengine.chat.service.NewLastDialog;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.chat.facade.MessageFacade;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ApiChatController {

    private final MessageFacade messageFacade;

    public ApiChatController(MessageFacade messageFacade) {
        this.messageFacade = messageFacade;
    }

    @DeleteMapping(path = "/message/{id}")
    public void deleteMessage(@PathVariable Long id,
                              @ApiIgnore @AuthenticationPrincipal User authUser) {
        messageFacade.deleteMessage(id, authUser.getUserId());
    }

    @PutMapping(path = "/message/{id}")
    public void editMessage(@PathVariable Long id, @RequestBody EditMessageRequest editMessageRequest,
                            @ApiIgnore @AuthenticationPrincipal User authUser) {
        messageFacade.editMessage(id, editMessageRequest.getContent(), authUser.getUserId());
    }

    @GetMapping(path = "/dialog/{dialogId}")
    public DialogView getMessages(@PathVariable Long dialogId,
                                  @RequestParam(required = false, defaultValue = "0") Integer page,
                                  @RequestParam(required = false, defaultValue = "50") Integer pageSize,
                                  @RequestParam(defaultValue = "timestamp") String sortBy,
                                  @ApiIgnore @AuthenticationPrincipal User authUser) {
        return messageFacade.getChatRoom(dialogId, page, pageSize, sortBy, authUser.getUserId());
    }

    @GetMapping(path = "/dialogs")
    public List<NewLastDialog> getDialogs(@ApiIgnore @AuthenticationPrincipal User authUser) {
        return messageFacade.getNewLastDialogs(authUser.getUserId());
    }

    @PostMapping(path = "/dialog/create")
    public Long checkDialog(@RequestParam Long userId, @ApiIgnore @AuthenticationPrincipal User authUser) {
        return messageFacade.createDialog(userId, authUser.getUserId());
    }

    @GetMapping("/dialog/{id}/attachments")
    public List<FileResponse> loadAttachments(@PathVariable Long id) {
        return messageFacade.getAttachments(id);
    }

}
