package com.example.mywebquizengine.photo.model.dto;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UploadPhotoResponse extends SuccessfulResponse {
    private UploadPhotoResult result;
}
