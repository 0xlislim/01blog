package com.blog.backend.dto.admin;

import java.time.LocalDateTime;

public class ReportResponse {

    private Long id;
    private String reason;
    private Long reporterId;
    private String reporterUsername;
    private Long reportedUserId;
    private String reportedUsername;
    private LocalDateTime createdAt;

    public ReportResponse() {
    }

    public ReportResponse(Long id, String reason, Long reporterId, String reporterUsername,
                         Long reportedUserId, String reportedUsername, LocalDateTime createdAt) {
        this.id = id;
        this.reason = reason;
        this.reporterId = reporterId;
        this.reporterUsername = reporterUsername;
        this.reportedUserId = reportedUserId;
        this.reportedUsername = reportedUsername;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getReportedUsername() {
        return reportedUsername;
    }

    public void setReportedUsername(String reportedUsername) {
        this.reportedUsername = reportedUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
