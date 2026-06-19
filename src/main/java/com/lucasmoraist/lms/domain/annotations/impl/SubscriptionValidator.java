package com.lucasmoraist.lms.domain.annotations.impl;

import com.lucasmoraist.lms.adapter.web.dto.payment.CreateSubscriptionDTO;
import com.lucasmoraist.lms.domain.annotations.ValidSubscription;
import com.lucasmoraist.lms.domain.enums.PaymentMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SubscriptionValidator implements ConstraintValidator<ValidSubscription, CreateSubscriptionDTO> {

    @Override
    public boolean isValid(CreateSubscriptionDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        if (PaymentMethod.CREDIT_CARD.equals(dto.paymentMethod()) && dto.subscription() == null) {

            // Desabilita a mensagem de erro genérica padrão no topo do JSON
            context.disableDefaultConstraintViolation();

            // Vincula o erro de validação especificamente ao nó do atributo "subscription"
            context.buildConstraintViolationWithTemplate("Subscription details are required for credit card payments")
                    .addPropertyNode("subscription")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }

}
