package com.blog.backend.dto.user;

import java.time.LocalDateTime;

public class UserProfileResponse {

    private Long id;
    private String username;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private String role;
    private Integer postCount;
    private Integer subscriberCount;
    private Integer subscribedToCount;
    private Boolean isSubscribed;
    private LocalDateTime createdAt;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String username, String displayName, String bio, String avatarUrl,
                              String role, Integer postCount, Integer subscriberCount, Integer subscribedToCount,
                              Boolean isSubscribed, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.postCount = postCount;
        this.subscriberCount = subscriberCount;
        this.subscribedToCount = subscribedToCount;
        this.isSubscribed = isSubscribed;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Integer getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(Integer subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public Integer getSubscribedToCount() {
        return subscribedToCount;
    }

    public void setSubscribedToCount(Integer subscribedToCount) {
        this.subscribedToCount = subscribedToCount;
    }

    public Boolean getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(Boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
