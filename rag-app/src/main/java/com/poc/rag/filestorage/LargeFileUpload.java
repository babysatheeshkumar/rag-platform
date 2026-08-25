package com.poc.rag.filestorage;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface LargeFileUpload {
    String uploadFile(MultipartFile file) throws IOException;
}
