package com.blog.backend.exception;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException() {
        super("Invalid file");
    }
}
