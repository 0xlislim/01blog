package com.blog.backend.exception;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String message) {
        super(message);
    }

    public ReportNotFoundException(Long reportId) {
        super("Report not found with ID: " + reportId);
    }
}
