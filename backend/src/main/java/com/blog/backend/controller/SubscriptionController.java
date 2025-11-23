package com.blog.backend.controller;

import com.blog.backend.dto.auth.MessageResponse;
import com.blog.backend.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<MessageResponse> subscribe(@PathVariable Long userId, Authentication authentication) {
        subscriptionService.subscribe(userId, authentication);
        return ResponseEntity.ok(new MessageResponse("Subscribed successfully"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<MessageResponse> unsubscribe(@PathVariable Long userId, Authentication authentication) {
        subscriptionService.unsubscribe(userId, authentication);
        return ResponseEntity.ok(new MessageResponse("Unsubscribed successfully"));
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<Boolean> isSubscribed(@PathVariable Long userId, Authentication authentication) {
        Boolean isSubscribed = subscriptionService.isSubscribed(userId, authentication);
        return ResponseEntity.ok(isSubscribed);
    }
}
