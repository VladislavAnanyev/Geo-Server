package com.example.meetings.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import static java.util.UUID.randomUUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service implements FileStorageService {

    private final AmazonS3 s3Client;
    @Value("${s3.bucket}")
    private String NAME_S3_LONG_TERM_BUCKET;

    /**
     * Сохранить файл в хранилище
     *
     * @param inputStream      инпут стрим
     * @param originalFilename пользовательское имя файла
     * @param contentType      тип контента файла
     * @return ресурс по которому доступен файл
     */
    @Override
    @SneakyThrows
    public String store(InputStream inputStream, String originalFilename, String contentType) {
        String filename = randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(inputStream.available());

        s3Client.putObject(NAME_S3_LONG_TERM_BUCKET, filename, inputStream, objectMetadata);

        return s3Client.getObject(NAME_S3_LONG_TERM_BUCKET, filename)
                .getObjectContent()
                .getHttpRequest()
                .getURI()
                .toString();
    }
}
