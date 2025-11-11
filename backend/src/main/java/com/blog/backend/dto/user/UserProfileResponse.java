package com.blog.backend.dto.user;

import java.time.LocalDateTime;

public class UserProfileResponse {

    private Long id;
    private String username;
    private String displayName;
    private String bio;
    private Integer postsCount;
    private Integer subscribersCount;
    private Integer subscriptionsCount;
    private Boolean isSubscribed;
    private LocalDateTime createdAt;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String username, String displayName, String bio,
                              Integer postsCount, Integer subscribersCount, Integer subscriptionsCount,
                              Boolean isSubscribed, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
        this.postsCount = postsCount;
        this.subscribersCount = subscribersCount;
        this.subscriptionsCount = subscriptionsCount;
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

    public Integer getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
    }

    public Integer getSubscribersCount() {
        return subscribersCount;
    }

    public void setSubscribersCount(Integer subscribersCount) {
        this.subscribersCount = subscribersCount;
    }

    public Integer getSubscriptionsCount() {
        return subscriptionsCount;
    }

    public void setSubscriptionsCount(Integer subscriptionsCount) {
        this.subscriptionsCount = subscriptionsCount;
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
