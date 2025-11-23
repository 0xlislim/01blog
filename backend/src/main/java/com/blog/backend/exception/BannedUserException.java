package com.blog.backend.exception;

public class BannedUserException extends RuntimeException {
    public BannedUserException(String message) {
        super(message);
    }

    public BannedUserException() {
        super("User is banned and cannot perform this action");
    }
}
