package com.poc.rag.filestorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3LargeFileUploadProvider implements LargeFileUpload {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;


    public String uploadFile(MultipartFile file) throws IOException {
        String key = UUID.randomUUID() + "-" + file.getOriginalFilename();

        log.info("S3 file upload : key : {}", key);
        log.info("S3 file upload : bucketName : {}", bucketName);
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(key)
                                   .contentType(file.getContentType()).build();


        PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        if (response.sdkHttpResponse().isSuccessful()) {
            return key;
        }

        return "";
    }
}
