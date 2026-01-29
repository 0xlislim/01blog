package com.blog.backend.service;

import com.blog.backend.dto.admin.AdminUserResponse;
import com.blog.backend.dto.admin.CreateReportRequest;
import com.blog.backend.dto.admin.ReportResponse;
import com.blog.backend.dto.post.PostResponse;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.Report;
import com.blog.backend.entity.User;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final LikeRepository likeRepository;
    private final NotificationRepository notificationRepository;
    private final SubscriptionRepository subscriptionRepository;

    public AdminService(UserRepository userRepository,
                       PostRepository postRepository,
                       ReportRepository reportRepository,
                       LikeRepository likeRepository,
                       NotificationRepository notificationRepository,
                       SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reportRepository = reportRepository;
        this.likeRepository = likeRepository;
        this.notificationRepository = notificationRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getDisplayName(),
                        user.getRole().name(),
                        user.getBanned(),
                        user.getPosts().size(),
                        subscriptionRepository.countBySubscribedToId(user.getId()).intValue(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setBanned(true);
        userRepository.save(user);
    }

    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setBanned(false);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Clear notification FK references to this user before deletion
        notificationRepository.deleteByRelatedPostUserId(userId);
        notificationRepository.nullifyRelatedUser(userId);

        userRepository.delete(user);
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getContent(),
                        post.getMediaUrl(),
                        post.getMediaType(),
                        post.getUser().getId(),
                        post.getUser().getUsername(),
                        post.getUser().getDisplayName(),
                        post.getLikes().size(),
                        post.getComments().size(),
                        false,
                        post.getHidden(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void hidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.setHidden(true);
        postRepository.save(post);
    }

    @Transactional
    public void unhidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.setHidden(false);
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        postRepository.delete(post);
    }

    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(report -> new ReportResponse(
                        report.getId(),
                        report.getReason(),
                        report.getReporter().getId(),
                        report.getReporter().getUsername(),
                        report.getReportedUser() != null ? report.getReportedUser().getId() : null,
                        report.getReportedUser() != null ? report.getReportedUser().getUsername() : null,
                        report.getReportedPost() != null ? report.getReportedPost().getId() : null,
                        report.getReportedPost() != null ? truncateContent(report.getReportedPost().getContent(), 100) : null,
                        report.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportResponse createReport(CreateReportRequest request, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User reporter = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(principal.getId()));

        Report report = new Report();
        report.setReason(request.getReason());
        report.setReporter(reporter);

        User reportedUser = null;
        Post reportedPost = null;

        if (request.getReportedPostId() != null) {
            reportedPost = postRepository.findById(request.getReportedPostId())
                    .orElseThrow(() -> new PostNotFoundException(request.getReportedPostId()));
            report.setReportedPost(reportedPost);

            if (reporter.getId().equals(reportedPost.getUser().getId())) {
                throw new ForbiddenException("You cannot report your own post");
            }
        } else if (request.getReportedUserId() != null) {
            reportedUser = userRepository.findById(request.getReportedUserId())
                    .orElseThrow(() -> new UserNotFoundException(request.getReportedUserId()));
            report.setReportedUser(reportedUser);

            if (reporter.getId().equals(reportedUser.getId())) {
                throw new ForbiddenException("You cannot report yourself");
            }
        } else {
            throw new IllegalArgumentException("Either reportedUserId or reportedPostId must be provided");
        }

        Report savedReport = reportRepository.save(report);

        return new ReportResponse(
                savedReport.getId(),
                savedReport.getReason(),
                reporter.getId(),
                reporter.getUsername(),
                reportedUser != null ? reportedUser.getId() : null,
                reportedUser != null ? reportedUser.getUsername() : null,
                reportedPost != null ? reportedPost.getId() : null,
                reportedPost != null ? truncateContent(reportedPost.getContent(), 100) : null,
                savedReport.getCreatedAt()
        );
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }

    @Transactional
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        reportRepository.delete(report);
    }
}
