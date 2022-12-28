package com.example.mywebquizengine.common.service;

import com.example.mywebquizengine.common.exception.FileNotFoundException;
import com.example.mywebquizengine.common.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.UUID.randomUUID;
import static org.springframework.util.StringUtils.cleanPath;

@Service
public class FileSystemStorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(@Value("${files.store}") String storagePath) {
        this.rootLocation = Paths.get(storagePath);
    }

    public String store(String contentType, String originalFilename, InputStream inputStream) {
        if (!contentType.equals("image/jpeg")) {
            throw new IllegalArgumentException("You must provide image/jpeg file");
        }

        String filename = randomUUID().toString().substring(0, 8) +
                          originalFilename.substring(originalFilename.lastIndexOf("."));

        filename = cleanPath(filename);
        try {
            try (inputStream) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }

        return filename;
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
