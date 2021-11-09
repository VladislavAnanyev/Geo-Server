package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Service.UserService;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Random;
import java.util.UUID;


@Controller
public class FileUploadController {

    @Autowired
    private UserService userService;

    @Value("${hostname}")
    private String hostname;


    @PostMapping(path = "/upload")
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal Principal principal) {
        String name = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                String uuid = UUID.randomUUID().toString();
                uuid = uuid.substring(0,8);
                byte[] bytes = file.getBytes();
                //fdfdf

                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("img/" +
                                uuid + ".jpg")));
                stream.write(bytes);
                stream.close();

                User user = userService.loadUserByUsernameProxy(principal.getName());

                userService.setAvatar("https://" + hostname + "/img/" + uuid + ".jpg", user);

                //file.transferTo(new File("C:/Users/avlad/IdeaProjects/WebQuiz" + name));

                User userLogin = userService.loadUserByUsernameProxy(principal.getName());
                //userLogin.setAvatar("https://" + hostname + "/img/" + uuid + ".jpg");
                model.addAttribute("user", userLogin);
                return "profile";
            } catch (Exception e) {
                return "Вам не удалось загрузить " + name + " => " + e.getMessage();
            }
        } else {
            return "Вам не удалось загрузить " + name + " потому что файл пустой.";
        }
    }



}
