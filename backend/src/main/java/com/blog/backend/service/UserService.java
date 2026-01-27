package com.blog.backend.service;

import com.blog.backend.dto.user.UpdateProfileRequest;
import com.blog.backend.dto.user.UserProfileResponse;
import com.blog.backend.entity.User;
import com.blog.backend.exception.ForbiddenException;
import com.blog.backend.exception.UserNotFoundException;
import com.blog.backend.repository.SubscriptionRepository;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public UserService(UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public UserProfileResponse getUserProfile(Long userId, Authentication authentication) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            currentUserId = principal.getId();
        }

        Boolean isSubscribed = false;
        if (currentUserId != null && !currentUserId.equals(userId)) {
            isSubscribed = subscriptionRepository.existsBySubscriberIdAndSubscribedToId(currentUserId, userId);
        }

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getBio(),
                user.getRole().name(),
                user.getPosts().size(),
                user.getSubscribers().size(),
                user.getSubscriptions().size(),
                isSubscribed,
                user.getCreatedAt()
        );
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        if (!principal.getId().equals(userId)) {
            throw new ForbiddenException("You can only update your own profile");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        User updatedUser = userRepository.save(user);

        return new UserProfileResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getDisplayName(),
                updatedUser.getBio(),
                updatedUser.getRole().name(),
                updatedUser.getPosts().size(),
                updatedUser.getSubscribers().size(),
                updatedUser.getSubscriptions().size(),
                false,
                updatedUser.getCreatedAt()
        );
    }

    public List<UserProfileResponse> searchUsers(String query, Authentication authentication) {
        List<User> users = userRepository.findAll();

        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            currentUserId = principal.getId();
        }

        final Long finalCurrentUserId = currentUserId;

        return users.stream()
                .filter(user -> query == null || query.isEmpty() ||
                        user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                        (user.getDisplayName() != null && user.getDisplayName().toLowerCase().contains(query.toLowerCase())))
                .filter(user -> !user.getBanned())
                .map(user -> {
                    Boolean isSubscribed = false;
                    if (finalCurrentUserId != null && !finalCurrentUserId.equals(user.getId())) {
                        isSubscribed = subscriptionRepository.existsBySubscriberIdAndSubscribedToId(finalCurrentUserId, user.getId());
                    }

                    return new UserProfileResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getDisplayName(),
                            user.getBio(),
                            user.getRole().name(),
                            user.getPosts().size(),
                            user.getSubscribers().size(),
                            user.getSubscriptions().size(),
                            isSubscribed,
                            user.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }
}
