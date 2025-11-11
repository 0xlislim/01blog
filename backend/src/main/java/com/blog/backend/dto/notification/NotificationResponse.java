package com.blog.backend.dto.notification;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String message;
    private String type;
    private Boolean read;
    private LocalDateTime createdAt;

    // Related post details (if applicable)
    private Long relatedPostId;
    private String relatedPostContent;

    // Related user details (if applicable)
    private Long relatedUserId;
    private String relatedUsername;
    private String relatedUserDisplayName;

    public NotificationResponse() {
    }

    public NotificationResponse(Long id, String message, String type, Boolean read,
                               LocalDateTime createdAt, Long relatedPostId, String relatedPostContent,
                               Long relatedUserId, String relatedUsername, String relatedUserDisplayName) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
        this.relatedPostId = relatedPostId;
        this.relatedPostContent = relatedPostContent;
        this.relatedUserId = relatedUserId;
        this.relatedUsername = relatedUsername;
        this.relatedUserDisplayName = relatedUserDisplayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getRelatedPostId() {
        return relatedPostId;
    }

    public void setRelatedPostId(Long relatedPostId) {
        this.relatedPostId = relatedPostId;
    }

    public String getRelatedPostContent() {
        return relatedPostContent;
    }

    public void setRelatedPostContent(String relatedPostContent) {
        this.relatedPostContent = relatedPostContent;
    }

    public Long getRelatedUserId() {
        return relatedUserId;
    }

    public void setRelatedUserId(Long relatedUserId) {
        this.relatedUserId = relatedUserId;
    }

    public String getRelatedUsername() {
        return relatedUsername;
    }

    public void setRelatedUsername(String relatedUsername) {
        this.relatedUsername = relatedUsername;
    }

    public String getRelatedUserDisplayName() {
        return relatedUserDisplayName;
    }

    public void setRelatedUserDisplayName(String relatedUserDisplayName) {
        this.relatedUserDisplayName = relatedUserDisplayName;
    }
}
