package com.lucasmoraist.lms.infrastructure.database.repository;

import com.lucasmoraist.lms.infrastructure.database.entity.payment.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {

}
