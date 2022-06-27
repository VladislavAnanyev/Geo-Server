package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.common.FileSystemStorageService;
import com.example.mywebquizengine.photo.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @PostMapping(path = "/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @AuthenticationPrincipal User principal) {

        String fileName = fileSystemStorageService.store(file);
        photoService.savePhoto(fileName, principal.getUserId());

        return "redirect:/profile";

    }


}
