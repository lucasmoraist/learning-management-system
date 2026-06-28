package com.lucasmoraist.lms.application.usecases.payment;

import com.lucasmoraist.lms.adapter.web.dto.payment.CreateSubscriptionDTO;
import com.lucasmoraist.lms.application.usecases.user.GetCurrentUserCase;
import com.lucasmoraist.lms.application.utils.IdempotencyUtils;
import com.lucasmoraist.lms.domain.enums.PaymentStatus;
import com.lucasmoraist.lms.domain.enums.RoleType;
import com.lucasmoraist.lms.domain.exceptions.PaymentFailedException;
import com.lucasmoraist.lms.domain.exceptions.PaymentProcessingException;
import com.lucasmoraist.lms.domain.gateway.CacheGateway;
import com.lucasmoraist.lms.domain.gateway.PaymentGateway;
import com.lucasmoraist.lms.domain.model.payment.PaymentResult;
import com.lucasmoraist.lms.domain.model.payment.Subscription;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.domain.model.user.Profile;
import com.lucasmoraist.lms.infrastructure.database.persistence.IdentityPersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.ProfilePersistence;
import com.lucasmoraist.lms.infrastructure.database.persistence.SubscriptionPersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateSubscriptionCase {

    private final GetCurrentUserCase getCurrentUserCase;
    private final CacheGateway cacheGateway;
    private final IdentityPersistence identityPersistence;
    private final SubscriptionPersistence subscriptionPersistence;
    private final PaymentGateway paymentGateway;
    private final ProfilePersistence profilePersistence;
    private final Executor processorAsync = Executors.newVirtualThreadPerTaskExecutor();

    public PaymentResult execute(String authorization, String traceId, CreateSubscriptionDTO createSubscription) {
        log.debug("[{}] - Executing CreateSubscriptionCase", traceId);

        Identity identity = this.getCurrentUserCase.execute(traceId, authorization);
        Profile profile = identity.getProfile();
        Subscription dynamicSubscription = profile.getSubscription();

        // 1. Guard Clause: If you already have an ACTIVE subscription (paid and current), please return immediately
        if (hasActiveSubscription(dynamicSubscription)) {
            log.info("[{}] - User with id {} already has an active subscription", traceId, identity.getId());
            return new PaymentResult(dynamicSubscription.getSubscriptionId(), dynamicSubscription.getStatus());
        }

        if (dynamicSubscription != null && PaymentStatus.PENDING.equals(dynamicSubscription.getStatus())) {
            log.warn("[{}] - Request blocked by idempotency validation for user {}", traceId, identity.getId());
            throw new PaymentProcessingException("Your subscription is already being processed. Please wait and try again.");
        }

        String idempotencyKey = IdempotencyUtils.generateIdempotencyKey(identity.getId().toString(), createSubscription, traceId);
        String lockKey = "idempotency:subscription:user:" + idempotencyKey;
        log.debug("[{}] - Generated idempotency key: {} and lock key: {}", traceId, idempotencyKey, lockKey);

        boolean isFirstRequest = this.cacheGateway.setIfAbsent(lockKey, PaymentStatus.PENDING.getStatus(), 40L);

        if (!isFirstRequest) {
            log.warn("[{}] - Duplicate subscription creation request detected for user with id {}. Idempotency key: {}",
                    traceId, identity.getId(), idempotencyKey);
            throw new PaymentProcessingException("Duplicate subscription creation request detected. Please wait and try again.");
        }

        try {
            PaymentResult result = this.paymentGateway.createSubscription(createSubscription, traceId);

            if (PaymentStatus.FAILED.equals(result.status())) {
                log.warn("[{}] - Gateway creation returned FAILED status immediately.", traceId);
                this.cacheGateway.delete(lockKey);
                return result;
            }

            Subscription subscriptionSaved;

            if (dynamicSubscription != null && PaymentStatus.CANCELED.equals(dynamicSubscription.getStatus())) {
                log.info("[{}] - Reactivating subscription ID: {}", traceId, dynamicSubscription.getSubscriptionId());

                dynamicSubscription.setStatus(result.status());
                dynamicSubscription.setPaymentMethod(createSubscription.paymentMethod());
                dynamicSubscription.setCurrentPeriodEnd(LocalDateTime.now().plusMonths(1));

                subscriptionSaved = this.subscriptionPersistence.save(dynamicSubscription, traceId);
            } else {
                log.info("[{}] - Creating a brand new subscription record.", traceId);

                subscriptionSaved = Subscription.builder()
                        .userId(profile)
                        .subscriptionId(result.externalSubscriptionId())
                        .status(result.status())
                        .paymentMethod(createSubscription.paymentMethod())
                        .currentPeriodEnd(LocalDateTime.now().plusMonths(1))
                        .build();

                subscriptionSaved = this.subscriptionPersistence.save(subscriptionSaved, traceId);
            }

            this.profilePersistence.updateSubscribe(profile, subscriptionSaved);

            this.processorAsync.execute(() -> this.processPaymentAsync(
                    identity,
                    result.externalSubscriptionId(),
                    traceId,
                    lockKey,
                    createSubscription
            ));

            return result;
        } catch (RuntimeException ex) {
            log.error("[{}] - Catastrophic error creating subscription for user {}: {}", traceId, identity.getId(), ex.getMessage());
            this.cacheGateway.delete(lockKey);
            throw ex;
        }
    }

    private boolean hasActiveSubscription(Subscription subscription) {
        if (subscription == null) {
            return false;
        }
        boolean isPaid = PaymentStatus.PAID.equals(subscription.getStatus());
        boolean isValidDate = !LocalDate.now().isAfter(subscription.getCurrentPeriodEnd().toLocalDate());

        return isPaid && isValidDate;
    }

    private void processPaymentAsync(
            Identity identity,
            String externalSubscriptionId,
            String traceId,
            String lockKey,
            CreateSubscriptionDTO createSubscription
    ) {
        PaymentResult result = this.paymentGateway.processPayment(createSubscription, traceId);

        try {
            if (!PaymentStatus.PAID.equals(result.status())) {
                log.warn("[{}] - Subscription creation failed for user with payment method {}",
                        traceId, createSubscription.paymentMethod());
                throw new PaymentFailedException("Subscription creation failed");
            }

            this.subscriptionPersistence.updatePaymentStatus(externalSubscriptionId, PaymentStatus.PAID, traceId);
            this.identityPersistence.updateRole(identity, RoleType.SUBSCRIBER);
        } catch (RuntimeException ex) {
            log.warn("[{}] - Error processing payment for subscription with id {}: {}",
                    traceId, externalSubscriptionId, ex.getMessage(), ex);
            this.subscriptionPersistence.updatePaymentStatus(externalSubscriptionId, PaymentStatus.FAILED, traceId);
        } finally {
            this.cacheGateway.delete(lockKey);
            log.debug("[{}] - Finished processing payment for subscription with id {}",
                    traceId, externalSubscriptionId);
        }
    }

}
