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

/**
 * Хранение файлов на локальной файловой системе.
 */
@Service
public class FileSystemStorageService implements FileStorageService {

    @Value("${files.store}")
    private Path rootLocation;

    /**
     * Сохраняет файл на локальную файловую систему.
     *
     * @param inputStream      поток содержимого файла
     * @param originalFilename исходное имя файла
     * @param contentType      MIME-тип файла
     * @return имя сохранённого файла
     */
    @Override
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
