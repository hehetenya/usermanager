package com.tetiana.usermanager.exception;

public class UserUnderAgeException extends RuntimeException {
    public UserUnderAgeException(String message) {
        super(message);
    }
}
