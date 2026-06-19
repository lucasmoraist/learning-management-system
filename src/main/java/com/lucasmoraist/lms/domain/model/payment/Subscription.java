package com.lucasmoraist.lms.domain.model.payment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lucasmoraist.lms.domain.enums.PaymentMethod;
import com.lucasmoraist.lms.domain.enums.PaymentStatus;
import com.lucasmoraist.lms.domain.model.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    private UUID id;
    @JsonBackReference
    private Profile userId;
    private String subscriptionId;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime currentPeriodEnd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
