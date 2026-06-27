package com.lucasmoraist.lms.application.utils;

import com.lucasmoraist.lms.adapter.web.dto.payment.CreateSubscriptionDTO;
import com.lucasmoraist.lms.adapter.web.dto.payment.CreditSubscriptionDTO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@UtilityClass
public class IdempotencyUtils {

    public static String generateIdempotencyKey(String identityId, CreateSubscriptionDTO dto, String traceId) {
        if (dto.subscription() instanceof CreditSubscriptionDTO creditSubscriptionDTO) {
            final String firstName = creditSubscriptionDTO.holderName().split(" ")[0];
            final String firstFourDigits = creditSubscriptionDTO.cardNumber().substring(0, 4);
            final String cvv = creditSubscriptionDTO.cvv();
            final String concatenatedString =  identityId + firstName + cvv + firstFourDigits;

            final String idempotencyKey = Base64.getEncoder().encodeToString(concatenatedString.getBytes(StandardCharsets.UTF_8));
            log.info("[{}] - [MOCK] Generated idempotency key: {}", traceId, idempotencyKey);
            return idempotencyKey;
        }
        log.warn("[{}] - [MOCK] Invalid subscription type for idempotency key generation", traceId);
        return null;
    }

}
