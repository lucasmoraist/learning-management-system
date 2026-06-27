package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.model.payment.Subscription;
import com.lucasmoraist.lms.domain.model.user.Profile;
import com.lucasmoraist.lms.infrastructure.database.entity.payment.SubscriptionEntity;
import com.lucasmoraist.lms.infrastructure.database.entity.user.ProfileEntity;
import com.lucasmoraist.lms.infrastructure.database.repository.ProfileRepository;
import com.lucasmoraist.lms.infrastructure.database.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProfilePersistence {

    private final ProfileRepository profileRepository;
    private final SubscriptionRepository subscriptionRepository;

    public ProfilePersistence(ProfileRepository profileRepository, SubscriptionRepository subscriptionRepository) {
        this.profileRepository = profileRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public void updateSubscribe(Profile profile, Subscription subscription) {
        ProfileEntity profileEntity = this.profileRepository.findById(profile.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        SubscriptionEntity subscriptionEntity = this.subscriptionRepository.findById(subscription.getId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        profileEntity.setSubscription(subscriptionEntity);
        this.profileRepository.save(profileEntity);
    }

}
