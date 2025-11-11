package com.blog.backend.service;

import com.blog.backend.dto.admin.AdminUserResponse;
import com.blog.backend.dto.admin.CreateReportRequest;
import com.blog.backend.dto.admin.ReportResponse;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.Report;
import com.blog.backend.entity.User;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.ReportRepository;
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

    public AdminService(UserRepository userRepository,
                       PostRepository postRepository,
                       ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reportRepository = reportRepository;
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
                        user.getReportsReceived().size(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBanned(true);
        userRepository.save(user);
    }

    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBanned(false);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        postRepository.delete(post);
    }

    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(report -> new ReportResponse(
                        report.getId(),
                        report.getReason(),
                        report.getReporter().getId(),
                        report.getReporter().getUsername(),
                        report.getReportedUser().getId(),
                        report.getReportedUser().getUsername(),
                        report.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportResponse createReport(CreateReportRequest request, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User reporter = userRepository.findById(principal.getId())
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        User reportedUser = userRepository.findById(request.getReportedUserId())
                .orElseThrow(() -> new RuntimeException("Reported user not found"));

        if (reporter.getId().equals(reportedUser.getId())) {
            throw new RuntimeException("You cannot report yourself");
        }

        Report report = new Report();
        report.setReason(request.getReason());
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);

        Report savedReport = reportRepository.save(report);

        return new ReportResponse(
                savedReport.getId(),
                savedReport.getReason(),
                reporter.getId(),
                reporter.getUsername(),
                reportedUser.getId(),
                reportedUser.getUsername(),
                savedReport.getCreatedAt()
        );
    }

    @Transactional
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        reportRepository.delete(report);
    }
}
