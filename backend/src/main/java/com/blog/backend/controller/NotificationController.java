package com.blog.backend.controller;

import com.blog.backend.dto.auth.MessageResponse;
import com.blog.backend.dto.notification.NotificationResponse;
import com.blog.backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(Authentication authentication) {
        List<NotificationResponse> notifications = notificationService.getUserNotifications(authentication);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(Authentication authentication) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(authentication);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        Long count = notificationService.getUnreadCount(authentication);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<MessageResponse> markAsRead(@PathVariable Long notificationId, Authentication authentication) {
        notificationService.markAsRead(notificationId, authentication);
        return ResponseEntity.ok(new MessageResponse("Notification marked as read"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<MessageResponse> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication);
        return ResponseEntity.ok(new MessageResponse("All notifications marked as read"));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<MessageResponse> deleteNotification(@PathVariable Long notificationId, Authentication authentication) {
        notificationService.deleteNotification(notificationId, authentication);
        return ResponseEntity.ok(new MessageResponse("Notification deleted successfully"));
    }
}
