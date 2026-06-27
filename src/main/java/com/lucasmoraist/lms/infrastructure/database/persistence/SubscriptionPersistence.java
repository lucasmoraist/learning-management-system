package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.enums.PaymentStatus;
import com.lucasmoraist.lms.domain.model.payment.Subscription;
import com.lucasmoraist.lms.infrastructure.database.entity.payment.SubscriptionEntity;
import com.lucasmoraist.lms.infrastructure.database.entity.user.ProfileEntity;
import com.lucasmoraist.lms.infrastructure.database.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubscriptionPersistence {

    private final SubscriptionRepository subscriptionRepository;
    private final ModelMapper modelMapper;

    public SubscriptionPersistence(SubscriptionRepository subscriptionRepository, ModelMapper modelMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.modelMapper = modelMapper;
    }

    public Subscription save(Subscription subscription, String traceId) {
        log.debug("[{}] - Saving subscription for user with id {}", traceId, subscription.getUserId().getId());

        SubscriptionEntity subscriptionEntity = this.modelMapper.map(subscription, SubscriptionEntity.class);

        ProfileEntity profileRef = new ProfileEntity();
        profileRef.setId(subscription.getUserId().getId());
        subscriptionEntity.setUserId(profileRef);

        this.subscriptionRepository.save(subscriptionEntity);
        log.debug("[{}] - Subscription for user with id {} saved successfully", traceId, subscription.getUserId().getId());

        return this.modelMapper.map(subscriptionEntity, Subscription.class);
    }

    public void updatePaymentStatus(String subscriptionId, PaymentStatus paymentStatus, String traceId) {
        SubscriptionEntity entity = findBySubscriptionId(subscriptionId, traceId);
        entity.setStatus(paymentStatus);
        this.subscriptionRepository.save(entity);
        log.debug("[{}] - Payment status for subscription with id {} updated successfully", traceId, subscriptionId);
    }

    private SubscriptionEntity findBySubscriptionId(String subscriptionId, String traceId) {
        return this.subscriptionRepository.findBySubscriptionId(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found for subscriptionId: " + subscriptionId));
    }

}
