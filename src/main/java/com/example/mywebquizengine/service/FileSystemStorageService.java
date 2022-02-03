package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.exception.FileNotFoundException;
import com.example.mywebquizengine.model.exception.StorageException;
import com.nimbusds.common.contenttype.ContentType;
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

@Service
public class FileSystemStorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(@Value("${files.store}") String storagePath) {
        this.rootLocation = Paths.get(storagePath);
    }

    public String store(MultipartFile file) {

        if (file.getContentType().equals("image/jpeg")) {
            String filename = UUID.randomUUID().toString().substring(0, 8) +
                    file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

            filename = StringUtils.cleanPath(filename);
            try {
                if (file.isEmpty()) {
                    throw new StorageException("Failed to store empty file " + filename);
                }
                if (filename.contains("..")) {
                    // This is a security check
                    throw new StorageException(
                            "Cannot store file with relative path outside current directory "
                                    + filename);
                }
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, this.rootLocation.resolve(filename),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
            catch (IOException e) {
                throw new StorageException("Failed to store file " + filename, e);
            }

            return filename;
        } else throw new IllegalArgumentException("You must provide image/jpeg file");

    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
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
            }
            else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
