package com.lucasmoraist.lms.infrastructure.payment;

import com.lucasmoraist.lms.adapter.web.dto.payment.CreateSubscriptionDTO;
import com.lucasmoraist.lms.adapter.web.dto.payment.CreditSubscriptionDTO;
import com.lucasmoraist.lms.domain.enums.PaymentMethod;
import com.lucasmoraist.lms.domain.enums.PaymentStatus;
import com.lucasmoraist.lms.domain.gateway.PaymentGateway;
import com.lucasmoraist.lms.domain.model.payment.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult createSubscription(CreateSubscriptionDTO dto, String traceId) {
        return switch (dto.paymentMethod()) {
            case CREDIT_CARD -> processCreditCardSubscription(dto, traceId);
            case DEBIT_CARD -> notImplementedYet(traceId, PaymentMethod.DEBIT_CARD);
            case PIX -> notImplementedYet(traceId, PaymentMethod.PIX);
        };
    }

    private PaymentResult notImplementedYet(String traceId, PaymentMethod paymentMethod) {
        log.warn("[{}] - [MOCK] {} payment method is not implemented yet", traceId, paymentMethod);
        return new PaymentResult(null, PaymentStatus.FAILED.getStatus());
    }

    // TODO: Implementar lógica para parcelas da assinatura, atualmente o mock só processa pagamentos à vista
    private PaymentResult processCreditCardSubscription(CreateSubscriptionDTO dto, String traceId) {
        if (dto.subscription() instanceof CreditSubscriptionDTO creditSubscriptionDTO) {
            log.info("[{}] - [MOCK] Creating subscription", traceId);

            try {
                Thread.sleep(500); // Simulate processing time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            final String cardNumber = creditSubscriptionDTO.cardNumber();

            if (cardNumber.endsWith("0000")) {
                log.warn("[{}] - [MOCK] Payment failed for card ending with 0000", traceId);
                return new PaymentResult(null, PaymentStatus.FAILED.getStatus());
            }

            final LocalDateTime today = LocalDateTime.now();
            final LocalDateTime expiryDate = LocalDateTime.of(
                    Integer.parseInt(creditSubscriptionDTO.expYear()),
                    Integer.parseInt(creditSubscriptionDTO.expMonth()),
                    1,
                    0,
                    0
            );

            if (expiryDate.isBefore(today)) {
                log.warn("[{}] - [MOCK] Payment failed due to expired card", traceId);
                return new PaymentResult(null, PaymentStatus.FAILED.getStatus());
            }

            String fakeSubscriptionId = "sub_mock_" + UUID.randomUUID().toString().substring(0, 8);
            log.info("[{}] - [MOCK] Subscription created successfully with ID: {}", traceId, fakeSubscriptionId);

            return new PaymentResult(fakeSubscriptionId, PaymentStatus.PAID.getStatus());
        } else {
            log.error("[{}] - [MOCK] Invalid subscription type for credit card payment", traceId);
            return new PaymentResult(null, PaymentStatus.FAILED.getStatus());
        }
    }

}
