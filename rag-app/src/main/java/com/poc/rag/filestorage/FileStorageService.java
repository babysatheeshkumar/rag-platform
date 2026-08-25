package com.poc.rag.filestorage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageProviderFactory factory;

    public String uploadLargeFile(MultipartFile file) throws IOException {
        return factory.getProvider().uploadFile(file);
    }
}
