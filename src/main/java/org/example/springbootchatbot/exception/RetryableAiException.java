package org.example.springbootchatbot.exception;

public class RetryableAiException extends RuntimeException {
    public RetryableAiException(String message) {
        super(message);
    }
}
