package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.chat.model.UploadAttachmentResponse;
import com.example.mywebquizengine.chat.model.dto.output.CreateDialogResponse;
import com.example.mywebquizengine.chat.model.dto.output.GetChatRoomResponse;
import com.example.mywebquizengine.chat.model.dto.output.GetDialogAttachmentsResponse;
import com.example.mywebquizengine.chat.model.dto.output.GetDialogsResponse;
import com.example.mywebquizengine.chat.facade.MessageFacade;
import com.example.mywebquizengine.chat.model.dto.input.EditMessageRequest;
import com.example.mywebquizengine.common.model.SuccessfulResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api/v1")
public class ApiChatController {

    private final MessageFacade messageFacade;

    public ApiChatController(MessageFacade messageFacade) {
        this.messageFacade = messageFacade;
    }

    @ApiOperation(value = "Удалить сообщение")
    @DeleteMapping(path = "/message/{id}")
    public SuccessfulResponse deleteMessage(@PathVariable Long id,
                                            @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        messageFacade.deleteMessage(id, authUser.getUserId());
        return new SuccessfulResponse();
    }

    @ApiOperation(value = "Изменить сообщение")
    @PutMapping(path = "/message/{id}")
    public SuccessfulResponse editMessage(@PathVariable Long id, @RequestBody EditMessageRequest editMessageRequest,
                                          @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        messageFacade.editMessage(id, editMessageRequest.getContent(), authUser.getUserId());
        return new SuccessfulResponse();
    }

    @ApiOperation(value = "Получить информацию о диалоге по его идентификатору")
    @GetMapping(path = "/dialog/{dialogId}")
    public GetChatRoomResponse getChatRoom(@PathVariable Long dialogId,
                                           @RequestParam(required = false, defaultValue = "0") Integer page,
                                           @RequestParam(required = false, defaultValue = "50") Integer pageSize,
                                           @RequestParam(defaultValue = "timestamp") String sortBy,
                                           @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        return new GetChatRoomResponse(
                messageFacade.getChatRoom(dialogId, page, pageSize, sortBy, authUser.getUserId())
        );
    }

    @ApiOperation(value = "Получить диалоги пользователя")
    @GetMapping(path = "/dialogs")
    public GetDialogsResponse getDialogs(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        return new GetDialogsResponse(
                messageFacade.getLastDialogs(authUser.getUserId())
        );
    }

    @ApiOperation(value = "Создать диалог")
    @PostMapping(path = "/dialog/create")
    public CreateDialogResponse createDialog(@RequestParam Long userId, @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        return new CreateDialogResponse(
                messageFacade.createDialog(userId, authUser.getUserId())
        );
    }

    @ApiOperation(value = "Получить вложения диалога")
    @GetMapping("/dialog/{id}/attachments")
    public GetDialogAttachmentsResponse getDialogAttachments(@PathVariable Long id) {
        return new GetDialogAttachmentsResponse(
                messageFacade.getAttachments(id)
        );
    }

    @PostMapping(path = "/dialog/photo")
    public UploadAttachmentResponse uploadPhotoInDialog(@RequestParam("file") MultipartFile file) throws IOException {
        return new UploadAttachmentResponse(
                messageFacade.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType())
        );
    }

}
