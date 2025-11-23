package com.blog.backend.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }

    public CommentNotFoundException(Long commentId) {
        super("Comment not found with ID: " + commentId);
    }
}
