package com.blog.backend.service;

import com.blog.backend.entity.Subscription;
import com.blog.backend.entity.User;
import com.blog.backend.enums.Role;
import com.blog.backend.exception.AlreadySubscribedException;
import com.blog.backend.exception.BannedUserException;
import com.blog.backend.exception.ForbiddenException;
import com.blog.backend.exception.NotSubscribedException;
import com.blog.backend.exception.UserNotFoundException;
import com.blog.backend.repository.SubscriptionRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private Authentication authentication;

    private User subscriber;
    private User subscribedTo;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        subscriber = new User();
        subscriber.setId(1L);
        subscriber.setUsername("subscriber");
        subscriber.setEmail("subscriber@example.com");
        subscriber.setDisplayName("Subscriber User");
        subscriber.setRole(Role.USER);
        subscriber.setBanned(false);
        subscriber.setPosts(new ArrayList<>());
        subscriber.setSubscribers(new ArrayList<>());
        subscriber.setSubscriptions(new ArrayList<>());

        subscribedTo = new User();
        subscribedTo.setId(2L);
        subscribedTo.setUsername("creator");
        subscribedTo.setEmail("creator@example.com");
        subscribedTo.setDisplayName("Creator User");
        subscribedTo.setRole(Role.USER);
        subscribedTo.setBanned(false);
        subscribedTo.setPosts(new ArrayList<>());
        subscribedTo.setSubscribers(new ArrayList<>());
        subscribedTo.setSubscriptions(new ArrayList<>());

        userPrincipal = new UserPrincipal(1L, "subscriber", "subscriber@example.com", "encodedPassword",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), false);
    }

    // subscribe Tests

    @Test
    void subscribe_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(userRepository.findById(2L)).thenReturn(Optional.of(subscribedTo));
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(1L, 2L)).thenReturn(false);

        // Act
        subscriptionService.subscribe(2L, authentication);

        // Assert
        verify(subscriptionRepository).save(any(Subscription.class));
        verify(notificationService).notifyNewSubscriber(subscribedTo, subscriber);
    }

    @Test
    void subscribe_ToSelf_ThrowsException() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        // Act & Assert
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> subscriptionService.subscribe(1L, authentication)
        );

        assertTrue(exception.getMessage().contains("yourself"));
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void subscribe_SubscriberNotFound_ThrowsException() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> subscriptionService.subscribe(2L, authentication)
        );

        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void subscribe_TargetUserNotFound_ThrowsException() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> subscriptionService.subscribe(999L, authentication)
        );

        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void subscribe_ToBannedUser_ThrowsException() {
        // Arrange
        subscribedTo.setBanned(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(userRepository.findById(2L)).thenReturn(Optional.of(subscribedTo));

        // Act & Assert
        BannedUserException exception = assertThrows(
                BannedUserException.class,
                () -> subscriptionService.subscribe(2L, authentication)
        );

        assertTrue(exception.getMessage().contains("banned"));
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void subscribe_AlreadySubscribed_ThrowsException() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(userRepository.findById(2L)).thenReturn(Optional.of(subscribedTo));
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(1L, 2L)).thenReturn(true);

        // Act & Assert
        assertThrows(
                AlreadySubscribedException.class,
                () -> subscriptionService.subscribe(2L, authentication)
        );

        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void subscribe_CreatesNotification() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(userRepository.findById(2L)).thenReturn(Optional.of(subscribedTo));
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(1L, 2L)).thenReturn(false);

        // Act
        subscriptionService.subscribe(2L, authentication);

        // Assert
        verify(notificationService).notifyNewSubscriber(subscribedTo, subscriber);
    }

    // unsubscribe Tests

    @Test
    void unsubscribe_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(1L, 2L)).thenReturn(true);

        // Act
        subscriptionService.unsubscribe(2L, authentication);

        // Assert
        verify(subscriptionRepository).deleteBySubscriberIdAndSubscribedToId(1L, 2L);
    }

    @Test
    void unsubscribe_NotSubscribed_ThrowsException() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(1L, 2L)).thenReturn(false);

        // Act & Assert
        assertThrows(
                NotSubscribedException.class,
                () -> subscriptionService.unsubscribe(2L, authentication)
        );

        verify(subscriptionRepository, never()).deleteBySubscriberIdAndSubscribedToId(anyLong(), anyLong());
    }

    // isSubscribed Tests

    @Test
    void isSubscribed_True() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(1L, 2L)).thenReturn(true);

        // Act
        Boolean result = subscriptionService.isSubscribed(2L, authentication);

        // Assert
        assertTrue(result);
        verify(subscriptionRepository).existsBySubscriberIdAndSubscribedToId(1L, 2L);
    }

    @Test
    void isSubscribed_False() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(1L, 2L)).thenReturn(false);

        // Act
        Boolean result = subscriptionService.isSubscribed(2L, authentication);

        // Assert
        assertFalse(result);
        verify(subscriptionRepository).existsBySubscriberIdAndSubscribedToId(1L, 2L);
    }
}
