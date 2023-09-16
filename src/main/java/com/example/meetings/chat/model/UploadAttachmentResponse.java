package com.example.meetings.chat.model;

import com.example.meetings.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadAttachmentResponse extends SuccessfulResponse {
    private UploadAttachmentResult result;
}
