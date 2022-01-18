package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.exception.LogicException;
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

    @PostMapping(path = "/user/photo/swap")
    public void swapPhoto(@ApiIgnore @AuthenticationPrincipal Principal principal,
                          @RequestParam Long firstId,
                          @RequestParam Long secondId) {
        photoService.swapPhoto(firstId, secondId, principal.getName());
    }

    @PostMapping(path = "/user/photo")
    public String uploadPhoto(@RequestParam("file") MultipartFile file,
                              @ApiIgnore @AuthenticationPrincipal Principal principal)
            throws LogicException, IOException {
        return photoService.uploadPhoto(file, principal.getName());
    }

    @DeleteMapping(path = "/user/photo/{id}")
    public void deletePhoto(@ApiIgnore @AuthenticationPrincipal Principal principal, @PathVariable Long id) throws LogicException {
        photoService.deletePhoto(id, principal.getName());
    }
}
