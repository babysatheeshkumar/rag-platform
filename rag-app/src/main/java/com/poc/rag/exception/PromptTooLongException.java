package com.poc.rag.exception;

public class PromptTooLongException extends RuntimeException {

    public PromptTooLongException(String message) {
        super(message);
    }
}
