package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.exception.LogicException;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.FileSystemStorageService;
import com.example.mywebquizengine.service.PhotoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = "/api")
public class ApiPhotoController {

    private final PhotoService photoService;
    private final FileSystemStorageService fileSystemStorageService;

    public ApiPhotoController(PhotoService photoService, FileSystemStorageService fileSystemStorageService) {
        this.photoService = photoService;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    @PostMapping(path = "/user/photo/swap")
    public void swapPhoto(@ApiIgnore @AuthenticationPrincipal User authUser,
                          @RequestParam Long photoId,
                          @RequestParam Integer position) {
        photoService.swapPhoto(photoId, position, authUser.getUserId());
    }

    @PostMapping(path = "/user/photo")
    public String uploadPhoto(@RequestParam("file") MultipartFile file,
                              @ApiIgnore @AuthenticationPrincipal User authUser) {
        String fileName = fileSystemStorageService.store(file);
        return photoService.savePhoto(fileName, authUser.getUserId());
    }

    @PostMapping(path = "/dialog/photo")
    public String uploadPhotoInDialog(@RequestParam("file") MultipartFile file) {
        return fileSystemStorageService.store(file);
    }

    @DeleteMapping(path = "/user/photo/{id}")
    public void deletePhoto(@ApiIgnore @AuthenticationPrincipal User authUser,
                            @PathVariable Long id) throws LogicException {
        photoService.deletePhoto(id, authUser.getUserId());
    }
}
