package com.blog.backend.service;

import com.blog.backend.dto.post.CommentRequest;
import com.blog.backend.dto.post.CommentResponse;
import com.blog.backend.dto.post.PostRequest;
import com.blog.backend.dto.post.PostResponse;
import com.blog.backend.entity.Comment;
import com.blog.backend.entity.Like;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.Subscription;
import com.blog.backend.entity.User;
import com.blog.backend.enums.Role;
import com.blog.backend.exception.BannedUserException;
import com.blog.backend.exception.CommentNotFoundException;
import com.blog.backend.exception.ForbiddenException;
import com.blog.backend.exception.PostNotFoundException;
import com.blog.backend.exception.UserNotFoundException;
import com.blog.backend.repository.*;
import com.blog.backend.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PostService postService;

    @Mock
    private Authentication authentication;

    private User user;
    private User anotherUser;
    private UserPrincipal userPrincipal;
    private Post post;
    private PostRequest postRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setDisplayName("Test User");
        user.setRole(Role.USER);
        user.setBanned(false);
        user.setPosts(new ArrayList<>());
        user.setSubscribers(new ArrayList<>());
        user.setSubscriptions(new ArrayList<>());

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setDisplayName("Another User");
        anotherUser.setRole(Role.USER);
        anotherUser.setBanned(false);
        anotherUser.setPosts(new ArrayList<>());
        anotherUser.setSubscribers(new ArrayList<>());
        anotherUser.setSubscriptions(new ArrayList<>());

        userPrincipal = new UserPrincipal(1L, "testuser", "test@example.com", "encodedPassword",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), false);

        post = new Post();
        post.setId(1L);
        post.setContent("Test post content");
        post.setUser(user);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRequest = new PostRequest();
        postRequest.setContent("Test post content");
        postRequest.setMediaUrl("http://example.com/image.jpg");
        postRequest.setMediaType("IMAGE");
    }

    // createPost Tests

    @Test
    void createPost_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(likeRepository.existsByUserIdAndPostId(anyLong(), anyLong())).thenReturn(false);

        // Act
        PostResponse response = postService.createPost(postRequest, authentication);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test post content", response.getContent());
        verify(userRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_BannedUser_ThrowsException() {
        // Arrange
        user.setBanned(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        BannedUserException exception = assertThrows(
                BannedUserException.class,
                () -> postService.createPost(postRequest, authentication)
        );

        assertTrue(exception.getMessage().contains("cannot create posts"));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void createPost_UserNotFound_ThrowsException() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> postService.createPost(postRequest, authentication)
        );

        verify(postRepository, never()).save(any(Post.class));
    }

    // getPost Tests

    @Test
    void getPost_Success_WithAuthentication() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(likeRepository.existsByUserIdAndPostId(1L, 1L)).thenReturn(true);

        // Act
        PostResponse response = postService.getPost(1L, authentication);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertTrue(response.getIsLiked());
        verify(postRepository).findById(1L);
    }

    @Test
    void getPost_Success_WithoutAuthentication() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        PostResponse response = postService.getPost(1L, null);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertFalse(response.getIsLiked());
        verify(likeRepository, never()).existsByUserIdAndPostId(anyLong(), anyLong());
    }

    @Test
    void getPost_NotFound_ThrowsException() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        PostNotFoundException exception = assertThrows(
                PostNotFoundException.class,
                () -> postService.getPost(999L, authentication)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    // updatePost Tests

    @Test
    void updatePost_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(likeRepository.existsByUserIdAndPostId(anyLong(), anyLong())).thenReturn(false);

        PostRequest updateRequest = new PostRequest();
        updateRequest.setContent("Updated content");

        // Act
        PostResponse response = postService.updatePost(1L, updateRequest, authentication);

        // Assert
        assertNotNull(response);
        assertEquals("Updated content", post.getContent());
        verify(postRepository).save(post);
    }

    @Test
    void updatePost_NotOwner_ThrowsException() {
        // Arrange
        post.setUser(anotherUser);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act & Assert
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> postService.updatePost(1L, postRequest, authentication)
        );

        assertTrue(exception.getMessage().contains("your own posts"));
        verify(postRepository, never()).save(any(Post.class));
    }

    // deletePost Tests

    @Test
    void deletePost_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        postService.deletePost(1L, authentication);

        // Assert
        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_NotOwner_ThrowsException() {
        // Arrange
        post.setUser(anotherUser);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act & Assert
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> postService.deletePost(1L, authentication)
        );

        assertTrue(exception.getMessage().contains("your own posts"));
        verify(postRepository, never()).delete(any(Post.class));
    }

    // getUserPosts Tests

    @Test
    void getUserPosts_Success() {
        // Arrange
        user.getPosts().add(post);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(likeRepository.existsByUserIdAndPostId(anyLong(), anyLong())).thenReturn(false);

        // Act
        List<PostResponse> results = postService.getUserPosts(1L, authentication);

        // Assert
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserPosts_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UserNotFoundException.class,
                () -> postService.getUserPosts(999L, authentication)
        );
    }

    // getFeed Tests

    @Test
    void getFeed_Success() {
        // Arrange
        Subscription subscription = new Subscription();
        subscription.setSubscriber(user);
        subscription.setSubscribedTo(anotherUser);

        Post anotherPost = new Post();
        anotherPost.setId(2L);
        anotherPost.setContent("Another post");
        anotherPost.setUser(anotherUser);
        anotherPost.setLikes(new ArrayList<>());
        anotherPost.setComments(new ArrayList<>());
        anotherPost.setCreatedAt(LocalDateTime.now());
        anotherPost.setUpdatedAt(LocalDateTime.now());

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(subscriptionRepository.findBySubscriberId(1L)).thenReturn(Arrays.asList(subscription));
        when(postRepository.findByUserIdInOrderByCreatedAtDesc(any())).thenReturn(Arrays.asList(post, anotherPost));
        when(likeRepository.existsByUserIdAndPostId(anyLong(), anyLong())).thenReturn(false);

        // Act
        List<PostResponse> results = postService.getFeed(authentication);

        // Assert
        assertEquals(2, results.size());
        verify(subscriptionRepository).findBySubscriberId(1L);
        verify(postRepository).findByUserIdInOrderByCreatedAtDesc(any());
    }

    // toggleLike Tests

    @Test
    void toggleLike_AddLike_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(likeRepository.existsByUserIdAndPostId(1L, 1L)).thenReturn(false);

        // Act
        postService.toggleLike(1L, authentication);

        // Assert
        verify(likeRepository).save(any(Like.class));
        verify(likeRepository, never()).deleteByUserIdAndPostId(anyLong(), anyLong());
    }

    @Test
    void toggleLike_RemoveLike_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(likeRepository.existsByUserIdAndPostId(1L, 1L)).thenReturn(true);

        // Act
        postService.toggleLike(1L, authentication);

        // Assert
        verify(likeRepository).deleteByUserIdAndPostId(1L, 1L);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void toggleLike_CreatesNotification() {
        // Arrange
        post.setUser(anotherUser);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(likeRepository.existsByUserIdAndPostId(1L, 1L)).thenReturn(false);

        // Act
        postService.toggleLike(1L, authentication);

        // Assert
        verify(notificationService).notifyNewLike(anotherUser, user, post);
    }

    // addComment Tests

    @Test
    void addComment_Success() {
        // Arrange
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("Test comment");

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setContent("Test comment");
        savedComment.setUser(user);
        savedComment.setPost(post);
        savedComment.setCreatedAt(LocalDateTime.now());

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // Act
        CommentResponse response = postService.addComment(1L, commentRequest, authentication);

        // Assert
        assertNotNull(response);
        assertEquals("Test comment", response.getContent());
        assertEquals(1L, response.getUserId());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_BannedUser_ThrowsException() {
        // Arrange
        user.setBanned(true);
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("Test comment");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        BannedUserException exception = assertThrows(
                BannedUserException.class,
                () -> postService.addComment(1L, commentRequest, authentication)
        );

        assertTrue(exception.getMessage().contains("cannot comment"));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_CreatesNotification() {
        // Arrange
        post.setUser(anotherUser);
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("Test comment");

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setContent("Test comment");
        savedComment.setUser(user);
        savedComment.setPost(post);
        savedComment.setCreatedAt(LocalDateTime.now());

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // Act
        postService.addComment(1L, commentRequest, authentication);

        // Assert
        verify(notificationService).notifyNewComment(anotherUser, user, post);
    }

    // getComments Tests

    @Test
    void getComments_Success() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment");
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        post.getComments().add(comment);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        List<CommentResponse> results = postService.getComments(1L);

        // Assert
        assertEquals(1, results.size());
        assertEquals("Test comment", results.get(0).getContent());
    }

    // deleteComment Tests

    @Test
    void deleteComment_Success() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment");
        comment.setUser(user);
        comment.setPost(post);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act
        postService.deleteComment(1L, authentication);

        // Assert
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_NotOwner_ThrowsException() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment");
        comment.setUser(anotherUser);
        comment.setPost(post);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act & Assert
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> postService.deleteComment(1L, authentication)
        );

        assertTrue(exception.getMessage().contains("your own comments"));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void deleteComment_NotFound_ThrowsException() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                CommentNotFoundException.class,
                () -> postService.deleteComment(999L, authentication)
        );
    }
}
