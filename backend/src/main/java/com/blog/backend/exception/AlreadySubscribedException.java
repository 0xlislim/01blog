package com.blog.backend.exception;

public class AlreadySubscribedException extends RuntimeException {
    public AlreadySubscribedException(String message) {
        super(message);
    }

    public AlreadySubscribedException() {
        super("You are already subscribed to this user");
    }
}
