package com.poc.rag.service;

import com.poc.rag.filestorage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LargeDocumentService {

    private final FileStorageService fileStorageService;

    public void uploadDocument(MultipartFile file) throws IOException {
        String sourceFileLocation = fileStorageService.uploadLargeFile(file);
        log.info("Uploaded file's source location : {}", sourceFileLocation);
        // TODO: Insert file metadata into postgres DB
    }

}
