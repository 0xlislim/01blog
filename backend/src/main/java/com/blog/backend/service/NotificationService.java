package com.blog.backend.service;

import com.blog.backend.dto.notification.NotificationResponse;
import com.blog.backend.entity.Notification;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.User;
import com.blog.backend.repository.NotificationRepository;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<NotificationResponse> getUserNotifications(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(principal.getId());

        return notifications.stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getUnreadNotifications(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        List<Notification> notifications = notificationRepository
                .findByUserIdAndRead(principal.getId(), false);

        return notifications.stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return notificationRepository.countByUserIdAndRead(principal.getId(), false);
    }

    @Transactional
    public void markAsRead(Long notificationId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(principal.getId())) {
            throw new RuntimeException("You can only mark your own notifications as read");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndRead(principal.getId(), false);

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(principal.getId())) {
            throw new RuntimeException("You can only delete your own notifications");
        }

        notificationRepository.delete(notification);
    }

    // Internal method to create notifications (called by other services)
    @Transactional
    public void createNotification(User recipient, String message, String type, Post relatedPost, User relatedUser) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setRelatedPost(relatedPost);
        notification.setRelatedUser(relatedUser);

        notificationRepository.save(notification);
    }

    // Helper method to create notification for new subscriber
    @Transactional
    public void notifyNewSubscriber(User subscribedTo, User subscriber) {
        String message = subscriber.getUsername() + " subscribed to you";
        createNotification(subscribedTo, message, "NEW_SUBSCRIBER", null, subscriber);
    }

    // Helper method to create notification for new like
    @Transactional
    public void notifyNewLike(User postOwner, User liker, Post post) {
        // Don't notify if user likes their own post
        if (postOwner.getId().equals(liker.getId())) {
            return;
        }

        String message = liker.getUsername() + " liked your post";
        createNotification(postOwner, message, "NEW_LIKE", post, liker);
    }

    // Helper method to create notification for new comment
    @Transactional
    public void notifyNewComment(User postOwner, User commenter, Post post) {
        // Don't notify if user comments on their own post
        if (postOwner.getId().equals(commenter.getId())) {
            return;
        }

        String message = commenter.getUsername() + " commented on your post";
        createNotification(postOwner, message, "NEW_COMMENT", post, commenter);
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        Long relatedPostId = null;
        String relatedPostContent = null;
        Long relatedUserId = null;
        String relatedUsername = null;
        String relatedUserDisplayName = null;

        if (notification.getRelatedPost() != null) {
            relatedPostId = notification.getRelatedPost().getId();
            // Truncate content to 100 characters for preview
            String content = notification.getRelatedPost().getContent();
            relatedPostContent = content.length() > 100
                ? content.substring(0, 100) + "..."
                : content;
        }

        if (notification.getRelatedUser() != null) {
            relatedUserId = notification.getRelatedUser().getId();
            relatedUsername = notification.getRelatedUser().getUsername();
            relatedUserDisplayName = notification.getRelatedUser().getDisplayName();
        }

        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getType(),
                notification.getRead(),
                notification.getCreatedAt(),
                relatedPostId,
                relatedPostContent,
                relatedUserId,
                relatedUsername,
                relatedUserDisplayName
        );
    }
}
