package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.model.exception.LogicException;
import com.example.mywebquizengine.service.PhotoService;
import com.example.mywebquizengine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;


@Controller
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserService userService;

    @PostMapping(path = "/upload")
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file,
                                   @AuthenticationPrincipal Principal principal) throws LogicException, IOException {

        photoService.uploadPhoto(file, principal.getName());

        User user = userService.loadUserByUsername(principal.getName());
        model.addAttribute("user", user);
        return "profile";

    }



}
