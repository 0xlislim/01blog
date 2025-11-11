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
    public ResponseEntity<?> subscribe(@PathVariable Long userId, Authentication authentication) {
        try {
            subscriptionService.subscribe(userId, authentication);
            return ResponseEntity.ok(new MessageResponse("Subscribed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long userId, Authentication authentication) {
        try {
            subscriptionService.unsubscribe(userId, authentication);
            return ResponseEntity.ok(new MessageResponse("Unsubscribed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<Boolean> isSubscribed(@PathVariable Long userId, Authentication authentication) {
        Boolean isSubscribed = subscriptionService.isSubscribed(userId, authentication);
        return ResponseEntity.ok(isSubscribed);
    }
}
