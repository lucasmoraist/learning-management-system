package com.lucasmoraist.lms.domain.model.payment;

public record PaymentResult(
        String externalSubscriptionId,
        String status
) {

}
