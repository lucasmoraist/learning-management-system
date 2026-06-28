package com.lucasmoraist.lms.adapter.web.controller;

import com.lucasmoraist.lms.adapter.web.dto.payment.CreateSubscriptionDTO;
import com.lucasmoraist.lms.application.usecases.payment.CancelSubscriptionCase;
import com.lucasmoraist.lms.application.usecases.payment.CreateSubscriptionCase;
import com.lucasmoraist.lms.application.utils.TraceIdUtils;
import com.lucasmoraist.lms.domain.model.payment.PaymentResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final CreateSubscriptionCase createSubscriptionCase;
    private final CancelSubscriptionCase cancelSubscriptionCase;

    @PostMapping("/subscribe")
    public ResponseEntity<PaymentResult> subscribe(@RequestHeader("Authorization") String authorization,
                                                   @Valid @RequestBody CreateSubscriptionDTO createSubscription) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Processing subscription for user with payment method {}", traceId, createSubscription.paymentMethod());

        PaymentResult result = this.createSubscriptionCase.execute(authorization, traceId, createSubscription);
        URI location = URI.create("/api/v1/auth/refresh-role");

        return ResponseEntity.created(location).body(result);
    }

    @PatchMapping("/{subscriptionId}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable UUID subscriptionId,
                                                   @RequestHeader("Authorization") String authorization) {
        String traceId = TraceIdUtils.generateTraceId();
        log.info("[{}] - Cancelling subscription for user with payment method {}", traceId, subscriptionId);
        
        this.cancelSubscriptionCase.execute(subscriptionId, authorization, traceId);
        return ResponseEntity.noContent().build();
    }

}
