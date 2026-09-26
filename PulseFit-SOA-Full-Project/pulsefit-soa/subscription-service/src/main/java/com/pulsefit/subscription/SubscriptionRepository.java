package com.pulsefit.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    List<Subscription> findByMemberId(Long memberId);
}