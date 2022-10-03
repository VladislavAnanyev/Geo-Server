package com.example.mywebquizengine.chat.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileResponse {
    private String originalName;
    private String filename;
    private String contentType;
}