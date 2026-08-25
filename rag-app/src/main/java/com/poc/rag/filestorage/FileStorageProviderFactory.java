package com.poc.rag.filestorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileStorageProviderFactory {

    private final List<LargeFileUpload> providers;

    @Value("${app.file-storage.provider}")
    private String source;


    public LargeFileUpload getProvider() {
        log.info("File storage name : {}", source);
        if (source.equals("s3")) {
            return find(S3LargeFileUploadProvider.class);
        }
        return null;
//        LargeFileUpload largeFileUpload;
//        switch (source) {
//            case "s3" ->
//                    largeFileUpload = find(S3LargeFileUploadImpl.class);
//            case "db" ->
//                    //
//            default ->
//                    //
//
//        }
//        return largeFileUpload;
    }

    private LargeFileUpload find(Class<?> type) {
        return providers.stream().filter(type::isInstance)
                .findFirst()
                .orElseThrow();
//        for (LargeFileUpload provider : providers) {
//            if (type.isInstance(provider)) {
//                return provider;
//            }
//        }
//        return null;
    }

}
