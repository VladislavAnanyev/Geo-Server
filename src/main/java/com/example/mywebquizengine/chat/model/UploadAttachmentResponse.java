package com.example.mywebquizengine.chat.model;

import com.example.mywebquizengine.chat.facade.UploadAttachmentResult;
import com.example.mywebquizengine.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadAttachmentResponse extends SuccessfulResponse {
    private UploadAttachmentResult result;
}
