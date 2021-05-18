package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.UUID;

import static com.example.mywebquizengine.Controller.QuizController.getAuthUser;

@Controller
public class FileUploadController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/upload")
    @ResponseBody
    public String provideUploadInfo() {
        return "profile";
    }

    @PostMapping(path = "/upload")
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file, Authentication authentication) {
        String name = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                String uuid = UUID.randomUUID().toString();
                uuid = uuid.substring(0,8);
                byte[] bytes = file.getBytes();

                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("img/" +
                                uuid + ".jpg")));
                stream.write(bytes);
                stream.close();

                User user = getAuthUser(authentication, userService);

                userService.setAvatar(uuid, user);

                //file.transferTo(new File("C:/Users/avlad/IdeaProjects/WebQuiz" + name));

                User userLogin = getAuthUser(authentication, userService);
                userLogin.setAvatar(uuid);
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
