package com.blog.backend.controller;

import com.blog.backend.dto.admin.AdminUserResponse;
import com.blog.backend.dto.admin.ReportResponse;
import com.blog.backend.dto.auth.MessageResponse;
import com.blog.backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        List<AdminUserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<MessageResponse> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok(new MessageResponse("User banned successfully"));
    }

    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<MessageResponse> unbanUser(@PathVariable Long userId) {
        adminService.unbanUser(userId);
        return ResponseEntity.ok(new MessageResponse("User unbanned successfully"));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable Long postId) {
        adminService.deletePost(postId);
        return ResponseEntity.ok(new MessageResponse("Post deleted successfully"));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        List<ReportResponse> reports = adminService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @DeleteMapping("/reports/{reportId}")
    public ResponseEntity<MessageResponse> deleteReport(@PathVariable Long reportId) {
        adminService.deleteReport(reportId);
        return ResponseEntity.ok(new MessageResponse("Report deleted successfully"));
    }
}
