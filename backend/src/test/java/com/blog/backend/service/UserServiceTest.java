package com.blog.backend.service;

import com.blog.backend.dto.user.UpdateProfileRequest;
import com.blog.backend.dto.user.UserProfileResponse;
import com.blog.backend.entity.User;
import com.blog.backend.enums.Role;
import com.blog.backend.exception.ForbiddenException;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private Authentication authentication;

    private User user;
    private User anotherUser;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setDisplayName("Test User");
        user.setBio("Test bio");
        user.setRole(Role.USER);
        user.setBanned(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setPosts(new ArrayList<>());
        user.setSubscribers(new ArrayList<>());
        user.setSubscriptions(new ArrayList<>());

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("encodedPassword");
        anotherUser.setDisplayName("Another User");
        anotherUser.setBio("Another bio");
        anotherUser.setRole(Role.USER);
        anotherUser.setBanned(false);
        anotherUser.setCreatedAt(LocalDateTime.now());
        anotherUser.setPosts(new ArrayList<>());
        anotherUser.setSubscribers(new ArrayList<>());
        anotherUser.setSubscriptions(new ArrayList<>());

        userPrincipal = new UserPrincipal(1L, "testuser", "test@example.com", "encodedPassword",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), false);
    }

    // getUserProfile Tests

    @Test
    void getUserProfile_Success_WithAuthentication() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(1L, 2L)).thenReturn(false);

        // Act
        UserProfileResponse response = userService.getUserProfile(2L, authentication);

        // Assert
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("anotheruser", response.getUsername());
        assertEquals("Another User", response.getDisplayName());
        assertEquals("Another bio", response.getBio());
        assertEquals(0, response.getPostsCount());
        assertEquals(0, response.getSubscribersCount());
        assertEquals(0, response.getSubscriptionsCount());
        assertFalse(response.getIsSubscribed());

        verify(userRepository).findById(2L);
        verify(subscriptionRepository).existsBySubscriberIdAndSubscribedToId(1L, 2L);
    }

    @Test
    void getUserProfile_Success_WithoutAuthentication() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserProfileResponse response = userService.getUserProfile(1L, null);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertFalse(response.getIsSubscribed());

        verify(userRepository).findById(1L);
        verify(subscriptionRepository, never()).existsBySubscriberIdAndSubscribedToId(anyLong(), anyLong());
    }

    @Test
    void getUserProfile_WithSubscription() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(1L, 2L)).thenReturn(true);

        // Act
        UserProfileResponse response = userService.getUserProfile(2L, authentication);

        // Assert
        assertTrue(response.getIsSubscribed());
        verify(subscriptionRepository).existsBySubscriberIdAndSubscribedToId(1L, 2L);
    }

    @Test
    void getUserProfile_ViewingOwnProfile() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        // Act
        UserProfileResponse response = userService.getUserProfile(1L, authentication);

        // Assert
        assertFalse(response.getIsSubscribed());
        verify(subscriptionRepository, never()).existsBySubscriberIdAndSubscribedToId(anyLong(), anyLong());
    }

    @Test
    void getUserProfile_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserProfile(999L, authentication)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(userRepository).findById(999L);
    }

    // updateProfile Tests

    @Test
    void updateProfile_Success_UpdateBoth() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setDisplayName("Updated Name");
        request.setBio("Updated bio");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserProfileResponse response = userService.updateProfile(1L, request, authentication);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Name", user.getDisplayName());
        assertEquals("Updated bio", user.getBio());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_Success_UpdateDisplayNameOnly() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setDisplayName("Updated Name");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserProfileResponse response = userService.updateProfile(1L, request, authentication);

        // Assert
        assertEquals("Updated Name", user.getDisplayName());
        assertEquals("Test bio", user.getBio()); // Original bio unchanged
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_Success_UpdateBioOnly() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBio("Updated bio");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserProfileResponse response = userService.updateProfile(1L, request, authentication);

        // Assert
        assertEquals("Test User", user.getDisplayName()); // Original name unchanged
        assertEquals("Updated bio", user.getBio());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_Forbidden_WhenUpdatingAnotherUser() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setDisplayName("Hacked Name");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        // Act & Assert
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> userService.updateProfile(2L, request, authentication)
        );

        assertTrue(exception.getMessage().contains("your own profile"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_UserNotFound_ThrowsException() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setDisplayName("Updated Name");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateProfile(1L, request, authentication)
        );

        assertTrue(exception.getMessage().contains("1"));
        verify(userRepository, never()).save(any(User.class));
    }

    // searchUsers Tests

    @Test
    void searchUsers_ByUsername() {
        // Arrange
        List<User> allUsers = Arrays.asList(user, anotherUser);
        when(userRepository.findAll()).thenReturn(allUsers);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(anyLong(), anyLong())).thenReturn(false);

        // Act
        List<UserProfileResponse> results = userService.searchUsers("another", authentication);

        // Assert
        assertEquals(1, results.size());
        assertEquals("anotheruser", results.get(0).getUsername());
        verify(userRepository).findAll();
    }

    @Test
    void searchUsers_ByDisplayName() {
        // Arrange
        List<User> allUsers = Arrays.asList(user, anotherUser);
        when(userRepository.findAll()).thenReturn(allUsers);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(anyLong(), anyLong())).thenReturn(false);

        // Act
        List<UserProfileResponse> results = userService.searchUsers("Another User", authentication);

        // Assert
        assertEquals(1, results.size());
        assertEquals("Another User", results.get(0).getDisplayName());
        verify(userRepository).findAll();
    }

    @Test
    void searchUsers_EmptyQuery_ReturnsAll() {
        // Arrange
        List<User> allUsers = Arrays.asList(user, anotherUser);
        when(userRepository.findAll()).thenReturn(allUsers);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(anyLong(), anyLong())).thenReturn(false);

        // Act
        List<UserProfileResponse> results = userService.searchUsers("", authentication);

        // Assert
        assertEquals(2, results.size());
        verify(userRepository).findAll();
    }

    @Test
    void searchUsers_NullQuery_ReturnsAll() {
        // Arrange
        List<User> allUsers = Arrays.asList(user, anotherUser);
        when(userRepository.findAll()).thenReturn(allUsers);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(anyLong(), anyLong())).thenReturn(false);

        // Act
        List<UserProfileResponse> results = userService.searchUsers(null, authentication);

        // Assert
        assertEquals(2, results.size());
        verify(userRepository).findAll();
    }

    @Test
    void searchUsers_FiltersBannedUsers() {
        // Arrange
        User bannedUser = new User();
        bannedUser.setId(3L);
        bannedUser.setUsername("banneduser");
        bannedUser.setDisplayName("Banned User");
        bannedUser.setBanned(true);
        bannedUser.setPosts(new ArrayList<>());
        bannedUser.setSubscribers(new ArrayList<>());
        bannedUser.setSubscriptions(new ArrayList<>());
        bannedUser.setCreatedAt(LocalDateTime.now());

        List<User> allUsers = Arrays.asList(user, anotherUser, bannedUser);
        when(userRepository.findAll()).thenReturn(allUsers);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(anyLong(), anyLong())).thenReturn(false);

        // Act
        List<UserProfileResponse> results = userService.searchUsers("", authentication);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().noneMatch(r -> r.getUsername().equals("banneduser")));
        verify(userRepository).findAll();
    }

    @Test
    void searchUsers_WithSubscriptionStatus() {
        // Arrange
        List<User> allUsers = Arrays.asList(user, anotherUser);
        when(userRepository.findAll()).thenReturn(allUsers);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(1L, 2L)).thenReturn(true);

        // Act
        List<UserProfileResponse> results = userService.searchUsers("", authentication);

        // Assert
        assertEquals(2, results.size());
        UserProfileResponse anotherUserResponse = results.stream()
                .filter(r -> r.getId().equals(2L))
                .findFirst()
                .orElseThrow();
        assertTrue(anotherUserResponse.getIsSubscribed());
        verify(subscriptionRepository).existsBySubscriberIdAndSubscribedToId(1L, 2L);
    }

    @Test
    void searchUsers_WithoutAuthentication() {
        // Arrange
        List<User> allUsers = Arrays.asList(user, anotherUser);
        when(userRepository.findAll()).thenReturn(allUsers);

        // Act
        List<UserProfileResponse> results = userService.searchUsers("", null);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> !r.getIsSubscribed()));
        verify(subscriptionRepository, never()).existsBySubscriberIdAndSubscribedToId(anyLong(), anyLong());
    }

    @Test
    void searchUsers_CaseInsensitive() {
        // Arrange
        List<User> allUsers = Arrays.asList(user, anotherUser);
        when(userRepository.findAll()).thenReturn(allUsers);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.existsBySubscriberIdAndSubscribedToId(anyLong(), anyLong())).thenReturn(false);

        // Act
        List<UserProfileResponse> results = userService.searchUsers("ANOTHER", authentication);

        // Assert
        assertEquals(1, results.size());
        assertEquals("anotheruser", results.get(0).getUsername());
    }
}
