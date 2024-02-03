package com.example.meetings.common.service;

import com.example.meetings.common.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.UUID.randomUUID;
import static org.springframework.util.StringUtils.cleanPath;

@Service
public class FileSystemStorageService implements FileStorageService {

    @Value("${files.store}")
    private Path rootLocation;

    public String store(InputStream inputStream, String originalFilename, String contentType) {
        String filename = randomUUID().toString().substring(0, 8) + originalFilename.substring(originalFilename.lastIndexOf("."));

        filename = cleanPath(filename);
        try {
            Files.copy(inputStream, this.rootLocation.resolve(filename), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }

        return filename;
    }
}
