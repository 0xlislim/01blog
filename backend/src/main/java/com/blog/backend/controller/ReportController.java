package com.blog.backend.controller;

import com.blog.backend.dto.admin.CreateReportRequest;
import com.blog.backend.dto.admin.ReportResponse;
import com.blog.backend.dto.auth.MessageResponse;
import com.blog.backend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ReportController {

    private final AdminService adminService;

    public ReportController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody CreateReportRequest request, Authentication authentication) {
        ReportResponse report = adminService.createReport(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }
}
