package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.model.payment.Subscription;
import com.lucasmoraist.lms.infrastructure.database.entity.payment.SubscriptionEntity;
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

    public void save(Subscription subscription, String traceId) {
        log.debug("[{}] - Saving subscription for user with id {}", traceId, subscription.getUserId().getId());
        SubscriptionEntity subscriptionEntity = this.modelMapper.map(subscription, SubscriptionEntity.class);
        this.subscriptionRepository.save(subscriptionEntity);
        log.debug("[{}] - Subscription for user with id {} saved successfully", traceId, subscription.getUserId().getId());
    }

}
