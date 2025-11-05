package com.blog.backend.repository;

import com.blog.backend.entity.Subscription;
import com.blog.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findBySubscriberAndSubscribedTo(User subscriber, User subscribedTo);

    Optional<Subscription> findBySubscriberIdAndSubscribedToId(Long subscriberId, Long subscribedToId);

    List<Subscription> findBySubscriber(User subscriber);

    List<Subscription> findBySubscriberId(Long subscriberId);

    List<Subscription> findBySubscribedTo(User subscribedTo);

    List<Subscription> findBySubscribedToId(Long subscribedToId);

    Boolean existsBySubscriberIdAndSubscribedToId(Long subscriberId, Long subscribedToId);

    void deleteBySubscriberIdAndSubscribedToId(Long subscriberId, Long subscribedToId);

    Long countBySubscribedToId(Long subscribedToId);
}
