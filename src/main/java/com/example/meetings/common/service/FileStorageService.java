package com.example.meetings.common.service;

import java.io.InputStream;

public interface FileStorageService {
    /**
     * Сохранить файл в хранилище
     *
     * @param originalFilename пользовательское имя файла
     * @param inputStream инпут стрим
     * @param contentType тип контента файла
     * @return ресурс по которому доступен файл
     */
    String store(InputStream inputStream, String originalFilename, String contentType);
}
