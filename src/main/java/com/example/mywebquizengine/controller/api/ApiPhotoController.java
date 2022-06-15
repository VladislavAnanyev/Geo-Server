package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.exception.LogicException;
import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.service.FileSystemStorageService;
import com.example.mywebquizengine.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping(path = "/api")
public class ApiPhotoController {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @PostMapping(path = "/user/photo/swap")
    public void swapPhoto(@ApiIgnore @AuthenticationPrincipal User authUser,
                          @RequestParam Long photoId,
                          @RequestParam Integer position) {
        photoService.swapPhoto(photoId, position, authUser.getUsername());
    }

    @PostMapping(path = "/user/photo")
    public String uploadPhoto(@RequestParam("file") MultipartFile file,
                              @ApiIgnore @AuthenticationPrincipal User authUser) {
        String fileName = fileSystemStorageService.store(file);
        return photoService.savePhoto(fileName, authUser.getUsername());
    }

    @PostMapping(path = "/dialog/photo")
    public String uploadPhotoInDialog(@RequestParam("file") MultipartFile file) {
        return fileSystemStorageService.store(file);
    }

    @DeleteMapping(path = "/user/photo/{id}")
    public void deletePhoto(@ApiIgnore @AuthenticationPrincipal User authUser,
                            @PathVariable Long id) throws LogicException {
        photoService.deletePhoto(id, authUser.getUsername());
    }
}
