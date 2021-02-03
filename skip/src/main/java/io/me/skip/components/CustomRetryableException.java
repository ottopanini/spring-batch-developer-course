package io.me.skip.components;

public class CustomRetryableException extends RuntimeException {
    public CustomRetryableException(String message) {
        super(message);
    }
}
