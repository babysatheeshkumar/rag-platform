package com.poc.rag.controller;

import com.poc.rag.service.DocumentService;
import com.poc.rag.service.LargeDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UploadController {

    private final DocumentService service;

    private final LargeDocumentService largeDocumentService;

    @Operation(
            summary = "Upload a document",
            description = "Uploads a PDF or text document to the vector store."
    )
    @ApiResponse(responseCode = "200", description = "Document uploaded successfully")
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String upload(
            @Parameter(
                    description = "Document file to upload",
                    content = @Content(
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestParam MultipartFile file)
            throws Exception {

        service.ingestDocumentToRAG(file);

        return "Document uploaded Successfully";
    }

    @Operation(
            summary = "Upload a document",
            description = "Uploads a PDF or text document into a datasource and store the file metadata " +
                          "into DB and document_id pushed to kafka topic." +
                          "Returns the file status. Later kafka consumes document_id and does " +
                          "process(content extraction, chunking, embedding and vector store)"
    )
    @ApiResponse(responseCode = "200", description = "Document uploaded successfully")
    @PostMapping(
            value = "/upload-large-file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String uploadLargeFile(
            @Parameter(
                    description = "Large Document file to upload. File size greater than 1MB",
                    content = @Content(
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestParam MultipartFile file) throws Exception {

        largeDocumentService.uploadDocument(file);

        return "Document uploaded Successfully";
    }
}
