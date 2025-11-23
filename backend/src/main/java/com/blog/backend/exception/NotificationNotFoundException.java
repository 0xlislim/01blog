package com.blog.backend.exception;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(String message) {
        super(message);
    }

    public NotificationNotFoundException(Long notificationId) {
        super("Notification not found with ID: " + notificationId);
    }
}
