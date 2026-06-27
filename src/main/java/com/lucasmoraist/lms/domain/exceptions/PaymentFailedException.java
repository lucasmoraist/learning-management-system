package com.lucasmoraist.lms.domain.exceptions;

public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(String message) {
        super(message);
    }

}
