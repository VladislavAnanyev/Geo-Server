package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.exception.EmptyFileException;
import com.example.mywebquizengine.model.userinfo.Photo;
import com.example.mywebquizengine.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping(path = "/api")
public class ApiPhotoController {

    @Autowired
    private PhotoService photoService;

    @PostMapping(path = "/user/photo/swap")
    public void swapPhoto(@ApiIgnore @AuthenticationPrincipal Principal principal, @RequestBody Photo photo) {
        photoService.swapPhoto(photo, principal.getName());
    }

    @PostMapping(path = "/user/photo")
    public String uploadPhoto(@RequestParam("file") MultipartFile file,
                              @ApiIgnore @AuthenticationPrincipal Principal principal)
            throws EmptyFileException, IOException {
        return photoService.uploadPhoto(file, principal.getName());
    }

    @DeleteMapping(path = "/user/photo/{id}")
    public void deletePhoto(@ApiIgnore @AuthenticationPrincipal Principal principal, @PathVariable Long id) {
        photoService.deletePhoto(id, principal.getName());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public void handleException(EntityNotFoundException e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalAccessError.class)
    public void handleException(IllegalAccessError e) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(EmptyFileException.class)
    public void handleException(EmptyFileException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public void handleException(IOException e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
