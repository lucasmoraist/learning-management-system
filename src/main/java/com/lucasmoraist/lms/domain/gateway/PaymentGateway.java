package com.lucasmoraist.lms.domain.gateway;

import com.lucasmoraist.lms.adapter.web.dto.payment.CreateSubscriptionDTO;
import com.lucasmoraist.lms.domain.model.payment.PaymentResult;

public interface PaymentGateway {

    PaymentResult createSubscription(CreateSubscriptionDTO dto, String traceId);
    PaymentResult processPayment(CreateSubscriptionDTO dto, String traceId);

}
