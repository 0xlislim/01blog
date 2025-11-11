package com.blog.backend.service;

import com.blog.backend.entity.Subscription;
import com.blog.backend.entity.User;
import com.blog.backend.repository.SubscriptionRepository;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               NotificationService notificationService) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void subscribe(Long userIdToSubscribe, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long currentUserId = principal.getId();

        if (currentUserId.equals(userIdToSubscribe)) {
            throw new RuntimeException("You cannot subscribe to yourself");
        }

        User subscriber = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User subscribedTo = userRepository.findById(userIdToSubscribe)
                .orElseThrow(() -> new RuntimeException("User to subscribe not found"));

        if (subscribedTo.getBanned()) {
            throw new RuntimeException("Cannot subscribe to banned user");
        }

        if (subscriptionRepository.existsBySubscriberIdAndSubscribedToId(currentUserId, userIdToSubscribe)) {
            throw new RuntimeException("Already subscribed to this user");
        }

        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setSubscribedTo(subscribedTo);

        subscriptionRepository.save(subscription);

        // Create notification for the user being subscribed to
        notificationService.notifyNewSubscriber(subscribedTo, subscriber);
    }

    @Transactional
    public void unsubscribe(Long userIdToUnsubscribe, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long currentUserId = principal.getId();

        if (!subscriptionRepository.existsBySubscriberIdAndSubscribedToId(currentUserId, userIdToUnsubscribe)) {
            throw new RuntimeException("You are not subscribed to this user");
        }

        subscriptionRepository.deleteBySubscriberIdAndSubscribedToId(currentUserId, userIdToUnsubscribe);
    }

    public Boolean isSubscribed(Long userIdToCheck, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long currentUserId = principal.getId();

        return subscriptionRepository.existsBySubscriberIdAndSubscribedToId(currentUserId, userIdToCheck);
    }
}
