package org.example.springbootchatbot.exception;

public class RetryableAiException extends RuntimeException {
    public RetryableAiException(String message) {
        super(message);
    }

    public RetryableAiException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryableAiException(Throwable cause) {
        super(cause);
    }
}
