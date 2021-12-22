package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;


@Controller
public class FileUploadController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/upload")
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file,
                                   @AuthenticationPrincipal Principal principal) {

        userService.uploadPhoto(file, principal.getName());

        User userLogin = userService.loadUserByUsernameProxy(principal.getName());
        //userLogin.setAvatar("https://" + hostname + "/img/" + uuid + ".jpg");
        model.addAttribute("user", userLogin);
        return "profile";

    }



}
