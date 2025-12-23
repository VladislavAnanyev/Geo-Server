package com.example.meetings.photo.model.dto;

import com.example.meetings.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UploadPhotoResponse extends SuccessfulResponse {
    private PhotoDto result;
}
