package com.blog.backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReportRequest {

    private Long reportedUserId;

    private Long reportedPostId;

    @NotBlank(message = "Reason is required")
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;

    public CreateReportRequest() {
    }

    public CreateReportRequest(Long reportedUserId, Long reportedPostId, String reason) {
        this.reportedUserId = reportedUserId;
        this.reportedPostId = reportedPostId;
        this.reason = reason;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public Long getReportedPostId() {
        return reportedPostId;
    }

    public void setReportedPostId(Long reportedPostId) {
        this.reportedPostId = reportedPostId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
