package com.lucasmoraist.lms.domain.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    PIX("Pix");

    private final String methodName;

    PaymentMethod(String methodName) {
        this.methodName = methodName;
    }
}
