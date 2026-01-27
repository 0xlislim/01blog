package com.blog.backend.service;

import com.blog.backend.dto.admin.AdminUserResponse;
import com.blog.backend.dto.admin.CreateReportRequest;
import com.blog.backend.dto.admin.ReportResponse;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.Report;
import com.blog.backend.entity.User;
import com.blog.backend.enums.Role;
import com.blog.backend.exception.ForbiddenException;
import com.blog.backend.exception.PostNotFoundException;
import com.blog.backend.exception.ReportNotFoundException;
import com.blog.backend.exception.UserNotFoundException;
import com.blog.backend.repository.LikeRepository;
import com.blog.backend.repository.NotificationRepository;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.ReportRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private AdminService adminService;

    @Mock
    private Authentication authentication;

    private User user;
    private User reportedUser;
    private UserPrincipal userPrincipal;
    private Post post;
    private Report report;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setDisplayName("Test User");
        user.setRole(Role.USER);
        user.setBanned(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setPosts(new ArrayList<>());
        user.setSubscribers(new ArrayList<>());
        user.setSubscriptions(new ArrayList<>());
        user.setReportsReceived(new ArrayList<>());

        reportedUser = new User();
        reportedUser.setId(2L);
        reportedUser.setUsername("reporteduser");
        reportedUser.setEmail("reported@example.com");
        reportedUser.setDisplayName("Reported User");
        reportedUser.setRole(Role.USER);
        reportedUser.setBanned(false);
        reportedUser.setCreatedAt(LocalDateTime.now());
        reportedUser.setPosts(new ArrayList<>());
        reportedUser.setSubscribers(new ArrayList<>());
        reportedUser.setSubscriptions(new ArrayList<>());
        reportedUser.setReportsReceived(new ArrayList<>());

        userPrincipal = new UserPrincipal(1L, "testuser", "test@example.com", "encodedPassword",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), false);

        post = new Post();
        post.setId(1L);
        post.setContent("Test post content");
        post.setUser(user);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        post.setCreatedAt(LocalDateTime.now());

        report = new Report();
        report.setId(1L);
        report.setReason("Inappropriate content");
        report.setReporter(user);
        report.setReportedUser(reportedUser);
        report.setCreatedAt(LocalDateTime.now());
    }

    // getAllUsers Tests

    @Test
    void getAllUsers_Success() {
        // Arrange
        List<User> users = Arrays.asList(user, reportedUser);
        when(userRepository.findAll()).thenReturn(users);
        when(subscriptionRepository.countBySubscribedToId(anyLong())).thenReturn(0L);

        // Act
        List<AdminUserResponse> results = adminService.getAllUsers();

        // Assert
        assertEquals(2, results.size());
        assertEquals("testuser", results.get(0).getUsername());
        assertEquals("reporteduser", results.get(1).getUsername());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_Empty() {
        // Arrange
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<AdminUserResponse> results = adminService.getAllUsers();

        // Assert
        assertTrue(results.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_IncludesStats() {
        // Arrange
        user.getPosts().add(post);
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);
        when(subscriptionRepository.countBySubscribedToId(user.getId())).thenReturn(0L);

        // Act
        List<AdminUserResponse> results = adminService.getAllUsers();

        // Assert
        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getPostCount());
        assertEquals(0, results.get(0).getSubscriberCount());
    }

    // banUser Tests

    @Test
    void banUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        adminService.banUser(1L);

        // Assert
        assertTrue(user.getBanned());
        verify(userRepository).save(user);
    }

    @Test
    void banUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> adminService.banUser(999L)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    // unbanUser Tests

    @Test
    void unbanUser_Success() {
        // Arrange
        user.setBanned(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        adminService.unbanUser(1L);

        // Assert
        assertFalse(user.getBanned());
        verify(userRepository).save(user);
    }

    @Test
    void unbanUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> adminService.unbanUser(999L)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    // deleteUser Tests

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        adminService.deleteUser(1L);

        // Assert
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> adminService.deleteUser(999L)
        );

        verify(userRepository, never()).delete(any(User.class));
    }

    // deletePost Tests

    @Test
    void deletePost_Success() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        adminService.deletePost(1L);

        // Assert
        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_PostNotFound_ThrowsException() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                PostNotFoundException.class,
                () -> adminService.deletePost(999L)
        );

        verify(postRepository, never()).delete(any(Post.class));
    }

    // getAllReports Tests

    @Test
    void getAllReports_Success() {
        // Arrange
        List<Report> reports = Arrays.asList(report);
        when(reportRepository.findAll()).thenReturn(reports);

        // Act
        List<ReportResponse> results = adminService.getAllReports();

        // Assert
        assertEquals(1, results.size());
        assertEquals("Inappropriate content", results.get(0).getReason());
        assertEquals("testuser", results.get(0).getReporterUsername());
        assertEquals("reporteduser", results.get(0).getReportedUsername());
        verify(reportRepository).findAll();
    }

    @Test
    void getAllReports_Empty() {
        // Arrange
        when(reportRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<ReportResponse> results = adminService.getAllReports();

        // Assert
        assertTrue(results.isEmpty());
        verify(reportRepository).findAll();
    }

    // createReport Tests

    @Test
    void createReport_Success() {
        // Arrange
        CreateReportRequest request = new CreateReportRequest();
        request.setReportedUserId(2L);
        request.setReason("Spam content");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(reportedUser));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        // Act
        ReportResponse response = adminService.createReport(request, authentication);

        // Assert
        assertNotNull(response);
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void createReport_ReporterNotFound_ThrowsException() {
        // Arrange
        CreateReportRequest request = new CreateReportRequest();
        request.setReportedUserId(2L);
        request.setReason("Spam content");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> adminService.createReport(request, authentication)
        );

        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    void createReport_ReportedUserNotFound_ThrowsException() {
        // Arrange
        CreateReportRequest request = new CreateReportRequest();
        request.setReportedUserId(999L);
        request.setReason("Spam content");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> adminService.createReport(request, authentication)
        );

        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    void createReport_SelfReport_ThrowsException() {
        // Arrange
        CreateReportRequest request = new CreateReportRequest();
        request.setReportedUserId(1L);
        request.setReason("Self report");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> adminService.createReport(request, authentication)
        );

        assertTrue(exception.getMessage().contains("yourself"));
        verify(reportRepository, never()).save(any(Report.class));
    }

    // deleteReport Tests

    @Test
    void deleteReport_Success() {
        // Arrange
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        // Act
        adminService.deleteReport(1L);

        // Assert
        verify(reportRepository).delete(report);
    }

    @Test
    void deleteReport_ReportNotFound_ThrowsException() {
        // Arrange
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ReportNotFoundException.class,
                () -> adminService.deleteReport(999L)
        );

        verify(reportRepository, never()).delete(any(Report.class));
    }
}
