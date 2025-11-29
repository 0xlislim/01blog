package com.blog.backend.service;

import com.blog.backend.dto.notification.NotificationResponse;
import com.blog.backend.entity.Notification;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.User;
import com.blog.backend.enums.Role;
import com.blog.backend.exception.ForbiddenException;
import com.blog.backend.exception.NotificationNotFoundException;
import com.blog.backend.repository.NotificationRepository;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private Authentication authentication;

    private User user;
    private User anotherUser;
    private UserPrincipal userPrincipal;
    private Notification notification;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setDisplayName("Test User");
        user.setRole(Role.USER);
        user.setBanned(false);

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setDisplayName("Another User");
        anotherUser.setRole(Role.USER);
        anotherUser.setBanned(false);

        userPrincipal = new UserPrincipal(1L, "testuser", "test@example.com", "encodedPassword",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), false);

        post = new Post();
        post.setId(1L);
        post.setContent("Test post content");
        post.setUser(user);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        post.setCreatedAt(LocalDateTime.now());

        notification = new Notification();
        notification.setId(1L);
        notification.setMessage("Test notification");
        notification.setType("NEW_LIKE");
        notification.setRead(false);
        notification.setUser(user);
        notification.setRelatedPost(post);
        notification.setRelatedUser(anotherUser);
        notification.setCreatedAt(LocalDateTime.now());
    }

    // getUserNotifications Tests

    @Test
    void getUserNotifications_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(notification));

        // Act
        List<NotificationResponse> results = notificationService.getUserNotifications(authentication);

        // Assert
        assertEquals(1, results.size());
        assertEquals("Test notification", results.get(0).getMessage());
        assertEquals("NEW_LIKE", results.get(0).getType());
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getUserNotifications_Empty() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(new ArrayList<>());

        // Act
        List<NotificationResponse> results = notificationService.getUserNotifications(authentication);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void getUserNotifications_WithRelatedPostAndUser() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(notification));

        // Act
        List<NotificationResponse> results = notificationService.getUserNotifications(authentication);

        // Assert
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getRelatedPostId());
        assertEquals("Test post content", results.get(0).getRelatedPostContent());
        assertEquals(2L, results.get(0).getRelatedUserId());
        assertEquals("anotheruser", results.get(0).getRelatedUsername());
        assertEquals("Another User", results.get(0).getRelatedUserDisplayName());
    }

    // getUnreadNotifications Tests

    @Test
    void getUnreadNotifications_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findByUserIdAndRead(1L, false))
                .thenReturn(Arrays.asList(notification));

        // Act
        List<NotificationResponse> results = notificationService.getUnreadNotifications(authentication);

        // Assert
        assertEquals(1, results.size());
        assertFalse(results.get(0).getRead());
        verify(notificationRepository).findByUserIdAndRead(1L, false);
    }

    // getUnreadCount Tests

    @Test
    void getUnreadCount_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.countByUserIdAndRead(1L, false)).thenReturn(5L);

        // Act
        Long count = notificationService.getUnreadCount(authentication);

        // Assert
        assertEquals(5L, count);
        verify(notificationRepository).countByUserIdAndRead(1L, false);
    }

    @Test
    void getUnreadCount_Zero() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.countByUserIdAndRead(1L, false)).thenReturn(0L);

        // Act
        Long count = notificationService.getUnreadCount(authentication);

        // Assert
        assertEquals(0L, count);
    }

    // markAsRead Tests

    @Test
    void markAsRead_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        // Act
        notificationService.markAsRead(1L, authentication);

        // Assert
        assertTrue(notification.getRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_NotFound_ThrowsException() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                NotificationNotFoundException.class,
                () -> notificationService.markAsRead(999L, authentication)
        );

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void markAsRead_NotOwner_ThrowsException() {
        // Arrange
        notification.setUser(anotherUser);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        // Act & Assert
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> notificationService.markAsRead(1L, authentication)
        );

        assertTrue(exception.getMessage().contains("your own notifications"));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    // markAllAsRead Tests

    @Test
    void markAllAsRead_Success() {
        // Arrange
        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setRead(false);
        notification2.setUser(user);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findByUserIdAndRead(1L, false))
                .thenReturn(Arrays.asList(notification, notification2));

        // Act
        notificationService.markAllAsRead(authentication);

        // Assert
        assertTrue(notification.getRead());
        assertTrue(notification2.getRead());
        verify(notificationRepository).saveAll(any());
    }

    @Test
    void markAllAsRead_NoUnread() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findByUserIdAndRead(1L, false))
                .thenReturn(new ArrayList<>());

        // Act
        notificationService.markAllAsRead(authentication);

        // Assert
        verify(notificationRepository).saveAll(new ArrayList<>());
    }

    // deleteNotification Tests

    @Test
    void deleteNotification_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        // Act
        notificationService.deleteNotification(1L, authentication);

        // Assert
        verify(notificationRepository).delete(notification);
    }

    @Test
    void deleteNotification_NotFound_ThrowsException() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                NotificationNotFoundException.class,
                () -> notificationService.deleteNotification(999L, authentication)
        );

        verify(notificationRepository, never()).delete(any(Notification.class));
    }

    @Test
    void deleteNotification_NotOwner_ThrowsException() {
        // Arrange
        notification.setUser(anotherUser);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        // Act & Assert
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> notificationService.deleteNotification(1L, authentication)
        );

        assertTrue(exception.getMessage().contains("your own notifications"));
        verify(notificationRepository, never()).delete(any(Notification.class));
    }

    // notifyNewSubscriber Tests

    @Test
    void notifyNewSubscriber_CreatesNotification() {
        // Arrange & Act
        notificationService.notifyNewSubscriber(user, anotherUser);

        // Assert
        verify(notificationRepository).save(argThat(n ->
                n.getMessage().contains("anotheruser") &&
                n.getMessage().contains("subscribed") &&
                n.getType().equals("NEW_SUBSCRIBER") &&
                n.getUser().equals(user) &&
                n.getRelatedUser().equals(anotherUser)
        ));
    }

    // notifyNewLike Tests

    @Test
    void notifyNewLike_CreatesNotification() {
        // Arrange & Act
        notificationService.notifyNewLike(user, anotherUser, post);

        // Assert
        verify(notificationRepository).save(argThat(n ->
                n.getMessage().contains("anotheruser") &&
                n.getMessage().contains("liked") &&
                n.getType().equals("NEW_LIKE") &&
                n.getUser().equals(user) &&
                n.getRelatedPost().equals(post) &&
                n.getRelatedUser().equals(anotherUser)
        ));
    }

    @Test
    void notifyNewLike_SelfLike_NoNotification() {
        // Arrange & Act
        notificationService.notifyNewLike(user, user, post);

        // Assert
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    // notifyNewComment Tests

    @Test
    void notifyNewComment_CreatesNotification() {
        // Arrange & Act
        notificationService.notifyNewComment(user, anotherUser, post);

        // Assert
        verify(notificationRepository).save(argThat(n ->
                n.getMessage().contains("anotheruser") &&
                n.getMessage().contains("commented") &&
                n.getType().equals("NEW_COMMENT") &&
                n.getUser().equals(user) &&
                n.getRelatedPost().equals(post) &&
                n.getRelatedUser().equals(anotherUser)
        ));
    }

    @Test
    void notifyNewComment_SelfComment_NoNotification() {
        // Arrange & Act
        notificationService.notifyNewComment(user, user, post);

        // Assert
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    // Content truncation test
    @Test
    void getUserNotifications_TruncatesLongContent() {
        // Arrange
        String longContent = "A".repeat(150);
        post.setContent(longContent);
        notification.setRelatedPost(post);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(notification));

        // Act
        List<NotificationResponse> results = notificationService.getUserNotifications(authentication);

        // Assert
        assertEquals(1, results.size());
        assertTrue(results.get(0).getRelatedPostContent().endsWith("..."));
        assertEquals(103, results.get(0).getRelatedPostContent().length()); // 100 chars + "..."
    }
}
