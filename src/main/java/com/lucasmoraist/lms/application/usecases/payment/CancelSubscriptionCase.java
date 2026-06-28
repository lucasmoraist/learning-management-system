package com.lucasmoraist.lms.application.usecases.payment;

import com.lucasmoraist.lms.application.usecases.user.GetCurrentUserCase;
import com.lucasmoraist.lms.domain.enums.PaymentStatus;
import com.lucasmoraist.lms.domain.enums.RoleType;
import com.lucasmoraist.lms.domain.model.payment.Subscription;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.SubscriptionPersistence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CancelSubscriptionCase {

    private final SubscriptionPersistence subscriptionPersistence;
    private final GetCurrentUserCase getCurrentUserCase;
    private final IdentityPersistence identityPersistence;

    public CancelSubscriptionCase(SubscriptionPersistence subscriptionPersistence, GetCurrentUserCase getCurrentUserCase, IdentityPersistence identityPersistence) {
        this.subscriptionPersistence = subscriptionPersistence;
        this.getCurrentUserCase = getCurrentUserCase;
        this.identityPersistence = identityPersistence;
    }

    public void execute(UUID subscriptionId, String authorization, String traceId) {
        log.debug("[{}] - Executing CancelSubscriptionCase for subscriptionId {}", traceId, subscriptionId);

        Subscription subscription = this.subscriptionPersistence.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        if (PaymentStatus.CANCELED.equals(subscription.getStatus())) {
            log.info("[{}] - Subscription with id {} is already canceled", traceId, subscriptionId);
            return;
        }
        Identity identity = this.getCurrentUserCase.execute(traceId, authorization);

        this.subscriptionPersistence.updatePaymentStatus(subscription.getSubscriptionId(), PaymentStatus.CANCELED, traceId);
        this.identityPersistence.updateRole(identity, RoleType.USER);

        log.info("[{}] - Subscription with id {} has been canceled successfully", traceId, subscriptionId);
    }

}
