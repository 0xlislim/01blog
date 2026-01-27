package com.blog.backend.repository;

import com.blog.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndRead(Long userId, Boolean read);

    Long countByUserIdAndRead(Long userId, Boolean read);

    @Modifying
    @Query("UPDATE Notification n SET n.relatedUser = NULL WHERE n.relatedUser.id = :userId")
    void nullifyRelatedUser(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.relatedPost.id IN (SELECT p.id FROM Post p WHERE p.user.id = :userId)")
    void deleteByRelatedPostUserId(@Param("userId") Long userId);
}
