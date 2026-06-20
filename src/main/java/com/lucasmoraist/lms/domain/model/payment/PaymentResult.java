package com.lucasmoraist.lms.domain.model.payment;

import com.lucasmoraist.lms.domain.enums.PaymentStatus;

public record PaymentResult(
        String externalSubscriptionId,
        PaymentStatus status
) {

}
