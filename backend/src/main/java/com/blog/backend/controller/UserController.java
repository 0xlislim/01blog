package com.blog.backend.controller;

import com.blog.backend.dto.user.UpdateProfileRequest;
import com.blog.backend.dto.user.UserProfileResponse;
import com.blog.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long userId,
            Authentication authentication) {
        UserProfileResponse profile = userService.getUserProfile(userId, authentication);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        UserProfileResponse profile = userService.updateProfile(userId, request, authentication);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileResponse>> searchUsers(
            @RequestParam(required = false) String query,
            Authentication authentication) {
        List<UserProfileResponse> users = userService.searchUsers(query, authentication);
        return ResponseEntity.ok(users);
    }
}
