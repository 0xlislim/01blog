package com.blog.backend.service;

import com.blog.backend.dto.auth.JwtResponse;
import com.blog.backend.dto.auth.LoginRequest;
import com.blog.backend.dto.auth.RegisterRequest;
import com.blog.backend.entity.User;
import com.blog.backend.enums.Role;
import com.blog.backend.exception.BannedUserException;
import com.blog.backend.exception.UserAlreadyExistsException;
import com.blog.backend.exception.UserNotFoundException;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Test User");
        registerRequest.setBio("Test bio");

        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setDisplayName("Test User");
        user.setBio("Test bio");
        user.setRole(Role.USER);
        user.setBanned(false);
    }

    @Test
    void register_Success() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenProvider.generateToken(anyLong(), anyString(), anyString())).thenReturn("jwt-token");

        // Act
        JwtResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals("USER", response.getRole());

        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider).generateToken(user.getId(), user.getUsername(), "USER");
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(registerRequest)
        );

        assertTrue(exception.getMessage().contains("username"));
        assertTrue(exception.getMessage().contains("testuser"));
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(registerRequest)
        );

        assertTrue(exception.getMessage().contains("email"));
        assertTrue(exception.getMessage().contains("test@example.com"));
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(anyLong(), anyString(), anyString())).thenReturn("jwt-token");

        // Act
        JwtResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals("USER", response.getRole());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail());
        verify(jwtTokenProvider).generateToken(user.getId(), user.getUsername(), "USER");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> authService.login(loginRequest)
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail());
        verify(jwtTokenProvider, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    void login_BannedUser_ThrowsException() {
        // Arrange
        user.setBanned(true);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));

        // Act & Assert
        BannedUserException exception = assertThrows(
                BannedUserException.class,
                () -> authService.login(loginRequest)
        );

        assertNotNull(exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail());
        verify(jwtTokenProvider, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    void login_WithEmail_Success() {
        // Arrange
        loginRequest.setUsernameOrEmail("test@example.com");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("test@example.com", "test@example.com"))
                .thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(anyLong(), anyString(), anyString())).thenReturn("jwt-token");

        // Act
        JwtResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        verify(userRepository).findByUsernameOrEmail("test@example.com", "test@example.com");
    }

    @Test
    void register_AssignsUserRole() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenProvider.generateToken(anyLong(), anyString(), anyString())).thenReturn("jwt-token");

        // Act
        authService.register(registerRequest);

        // Assert
        verify(userRepository).save(argThat(u ->
                u.getRole() == Role.USER &&
                u.getBanned() == false &&
                u.getUsername().equals("testuser")
        ));
    }
}
