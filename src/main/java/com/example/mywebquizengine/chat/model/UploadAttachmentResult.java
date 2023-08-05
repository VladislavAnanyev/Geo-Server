package com.example.mywebquizengine.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UploadAttachmentResult {
    private String uri;
}
