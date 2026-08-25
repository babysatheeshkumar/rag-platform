package com.poc.rag.exception;

import com.poc.rag.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileUploadSize;

    @ExceptionHandler(PromptTooLongException.class)
    public ResponseEntity<ErrorResponse> handlePromptTooLong(
            PromptTooLongException ex) {

        log.error("Prompt too long", ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "PROMPT_TOO_LONG",
                        ex.getMessage()
                ));
    }

    public ResponseEntity<ErrorResponse> handleDuplicateFile(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("DUPLICATE_FILE", ex.getMessage()));
    }

    public ResponseEntity<ErrorResponse> handleMaximumFileSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(new ErrorResponse("MAXIMUM_FILE_SIZE",
                ex.getMessage().concat(" Upload file maximum size : ") + maxFileUploadSize));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleFileTypeNotSupported(IllegalArgumentException ex) {

        log.error("Unsupported file type", ex);
        String code = "";
        if (ex.getMessage().contains("Unsupported file type")) {
            code = "UNSUPPORTED_FILE_TYPE";
        } else {
            code = "ILLEGAL_ARGUMENT";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(code, ex.getMessage()));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {

        log.error("handleGeneric Unexpected error", ex);

        if (ex instanceof DuplicateFileException) {
            return handleDuplicateFile(ex);
        }

        if (ex instanceof MaxUploadSizeExceededException) {
            return handleMaximumFileSize((MaxUploadSizeExceededException) ex);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("INTERNAL_ERROR","Unexpected error occurred"));
    }
}