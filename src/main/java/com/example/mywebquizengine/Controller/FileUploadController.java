package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.UUID;

@Controller
public class FileUploadController {

    @Autowired
    private UserService userService;

    //@RequestMapping(value="/upload", method= RequestMethod.GET)
    @GetMapping(path = "/upload")
    @ResponseBody
    public String provideUploadInfo() {
        return "profile";
    }

    //@RequestMapping(value="/upload", method=RequestMethod.POST)
    @PostMapping(path = "/upload")
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file) {
        String name = file.getOriginalFilename();
        //File file1 = new File("C:\\Users\\avlad\\IdeaProjects\\WebQuiz\\src\\main\\resources\\static\\img\\" + userService.getThisUser().getAvatar() + ".jpg");

        if (!file.isEmpty()) {
            //name = userService.getThisUser().getAvatar();
            try {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String uuid = UUID.randomUUID().toString();
                uuid = uuid.substring(0,8);
                byte[] bytes = file.getBytes();

                //System.out.println(file.getContentType().);

                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("img/" +
                                uuid + ".jpg")));
                stream.write(bytes);
                stream.close();

                userService.setAvatar(uuid);

                //file.transferTo(new File("C:/Users/avlad/IdeaProjects/WebQuiz" + name));

                User userLogin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
