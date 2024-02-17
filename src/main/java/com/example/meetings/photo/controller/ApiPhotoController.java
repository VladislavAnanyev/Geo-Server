package com.example.meetings.photo.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.photo.facade.PhotoFacade;
import com.example.meetings.photo.model.dto.UploadPhotoResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api/v1")
public class ApiPhotoController {

    private final PhotoFacade photoFacade;

    public ApiPhotoController(PhotoFacade photoFacade) {
        this.photoFacade = photoFacade;
    }

    @ApiOperation(value = "Поставить фотографию на указанную позицию в списке фотографий пользователя")
    @PostMapping(path = "/user/photo/swap")
    public SuccessfulResponse swapPhoto(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                        @RequestParam Long photoId,
                                        @RequestParam Integer position) {
        photoFacade.swapPhoto(photoId, position, authUser.getUserId());
        return new SuccessfulResponse();
    }

    @ApiOperation(value = "Загрузить фотографию")
    @PostMapping(path = "/user/photo")
    public UploadPhotoResponse uploadPhoto(@RequestParam("file") MultipartFile file,
                                           @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) throws IOException {
        return new UploadPhotoResponse(
                photoFacade.uploadPhoto(
                        file.getInputStream(),
                        file.getOriginalFilename(),
                        file.getContentType(),
                        authUser.getUserId()
                )
        );
    }

    @ApiOperation(value = "Удалить фотографию")
    @DeleteMapping(path = "/user/photo/{id}")
    public SuccessfulResponse deletePhoto(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser, @PathVariable Long id) {
        photoFacade.deletePhoto(id, authUser.getUserId());
        return new SuccessfulResponse();
    }
}
