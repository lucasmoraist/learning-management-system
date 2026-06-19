package com.lucasmoraist.lms.adapter.web.dto.payment;

import com.lucasmoraist.lms.domain.annotations.ValidSubscription;
import com.lucasmoraist.lms.domain.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@ValidSubscription
public record CreateSubscriptionDTO(
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,
        @Valid
        SubscriptionDTO subscription
) {

}
