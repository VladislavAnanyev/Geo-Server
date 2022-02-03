package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.service.FileSystemStorageService;
import com.example.mywebquizengine.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;


@Controller
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @PostMapping(path = "/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @AuthenticationPrincipal Principal principal) {

        String fileName = fileSystemStorageService.store(file);
        photoService.savePhoto(fileName, principal.getName());

        return "redirect:/profile";

    }


}
