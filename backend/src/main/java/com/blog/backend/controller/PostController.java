package com.blog.backend.controller;

import com.blog.backend.dto.auth.MessageResponse;
import com.blog.backend.dto.post.CommentRequest;
import com.blog.backend.dto.post.CommentResponse;
import com.blog.backend.dto.post.PostRequest;
import com.blog.backend.dto.post.PostResponse;
import com.blog.backend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request, Authentication authentication) {
        PostResponse post = postService.createPost(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId, Authentication authentication) {
        PostResponse post = postService.getPost(postId, authentication);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request,
            Authentication authentication) {
        PostResponse post = postService.updatePost(postId, request, authentication);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable Long postId, Authentication authentication) {
        postService.deletePost(postId, authentication);
        return ResponseEntity.ok(new MessageResponse("Post deleted successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable Long userId, Authentication authentication) {
        List<PostResponse> posts = postService.getUserPosts(userId, authentication);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getFeed(Authentication authentication) {
        List<PostResponse> posts = postService.getFeed(authentication);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> toggleLike(@PathVariable Long postId, Authentication authentication) {
        PostResponse post = postService.toggleLike(postId, authentication);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        CommentResponse comment = postService.addComment(postId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        List<CommentResponse> comments = postService.getComments(postId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        postService.deleteComment(commentId, authentication);
        return ResponseEntity.ok(new MessageResponse("Comment deleted successfully"));
    }
}
