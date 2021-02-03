package io.me.retry.components;

public class CustomRetryableException extends RuntimeException {
    public CustomRetryableException(String message) {
        super(message);
    }
}
