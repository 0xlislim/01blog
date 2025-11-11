package com.blog.backend.repository;

import com.blog.backend.entity.Like;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostAndUser(Post post, User user);

    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);

    List<Like> findByPost(Post post);

    List<Like> findByPostId(Long postId);

    Long countByPostId(Long postId);

    Boolean existsByPostIdAndUserId(Long postId, Long userId);

    Boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByPostIdAndUserId(Long postId, Long userId);

    void deleteByUserIdAndPostId(Long userId, Long postId);
}
