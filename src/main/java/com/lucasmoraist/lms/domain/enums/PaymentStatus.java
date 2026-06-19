package com.lucasmoraist.lms.domain.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("Pending"),
    PAID("Paid"),
    CANCELED("Canceled");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }
}
