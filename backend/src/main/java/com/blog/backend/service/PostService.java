package com.blog.backend.service;

import com.blog.backend.dto.post.CommentRequest;
import com.blog.backend.dto.post.CommentResponse;
import com.blog.backend.dto.post.PostRequest;
import com.blog.backend.dto.post.PostResponse;
import com.blog.backend.entity.Comment;
import com.blog.backend.entity.Like;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.User;
import com.blog.backend.exception.BannedUserException;
import com.blog.backend.exception.CommentNotFoundException;
import com.blog.backend.exception.ForbiddenException;
import com.blog.backend.exception.PostNotFoundException;
import com.blog.backend.exception.UserNotFoundException;
import com.blog.backend.repository.*;
import com.blog.backend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    public PostService(PostRepository postRepository,
                      UserRepository userRepository,
                      LikeRepository likeRepository,
                      CommentRepository commentRepository,
                      SubscriptionRepository subscriptionRepository,
                      NotificationService notificationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public PostResponse createPost(PostRequest request, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(principal.getId()));

        if (user.getBanned()) {
            throw new BannedUserException("Banned users cannot create posts");
        }

        Post post = new Post();
        post.setContent(request.getContent());
        post.setMediaUrl(request.getMediaUrl());
        post.setMediaType(request.getMediaType());
        post.setUser(user);

        Post savedPost = postRepository.save(post);

        return mapToPostResponse(savedPost, principal.getId());
    }

    public PostResponse getPost(Long postId, Authentication authentication) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            currentUserId = principal.getId();
        }

        return mapToPostResponse(post, currentUserId);
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getUser().getId().equals(principal.getId())) {
            throw new ForbiddenException("You can only update your own posts");
        }

        post.setContent(request.getContent());
        post.setMediaUrl(request.getMediaUrl());
        post.setMediaType(request.getMediaType());

        Post updatedPost = postRepository.save(post);

        return mapToPostResponse(updatedPost, principal.getId());
    }

    @Transactional
    public void deletePost(Long postId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getUser().getId().equals(principal.getId())) {
            throw new ForbiddenException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }

    public List<PostResponse> getUserPosts(Long userId, Authentication authentication) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            currentUserId = principal.getId();
        }

        final Long finalCurrentUserId = currentUserId;

        return user.getPosts().stream()
                .map(post -> mapToPostResponse(post, finalCurrentUserId))
                .collect(Collectors.toList());
    }

    public List<PostResponse> getFeed(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        List<Long> subscribedUserIds = subscriptionRepository
                .findBySubscriberId(principal.getId())
                .stream()
                .map(sub -> sub.getSubscribedTo().getId())
                .collect(Collectors.toList());

        subscribedUserIds.add(principal.getId());

        List<Post> posts = postRepository.findByUserIdInOrderByCreatedAtDesc(subscribedUserIds);

        return posts.stream()
                .map(post -> mapToPostResponse(post, principal.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleLike(Long postId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(principal.getId()));

        if (likeRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            likeRepository.deleteByUserIdAndPostId(user.getId(), post.getId());
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);

            // Create notification for post owner
            notificationService.notifyNewLike(post.getUser(), user, post);
        }
    }

    @Transactional
    public CommentResponse addComment(Long postId, CommentRequest request, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(principal.getId()));

        if (user.getBanned()) {
            throw new BannedUserException("Banned users cannot comment");
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);

        // Create notification for post owner
        notificationService.notifyNewComment(post.getUser(), user, post);

        return new CommentResponse(
                savedComment.getId(),
                savedComment.getContent(),
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                savedComment.getCreatedAt()
        );
    }

    public List<CommentResponse> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return post.getComments().stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUser().getId(),
                        comment.getUser().getUsername(),
                        comment.getUser().getDisplayName(),
                        comment.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getUser().getId().equals(principal.getId())) {
            throw new ForbiddenException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    private PostResponse mapToPostResponse(Post post, Long currentUserId) {
        Boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = likeRepository.existsByUserIdAndPostId(currentUserId, post.getId());
        }

        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getMediaUrl(),
                post.getMediaType(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getUser().getDisplayName(),
                post.getLikes().size(),
                post.getComments().size(),
                isLiked,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
