package com.poc.rag.exception;

import org.jspecify.annotations.Nullable;

public class DuplicateFileException extends RuntimeException {
    public DuplicateFileException(@Nullable String s) {
        super(s);
    }
}
