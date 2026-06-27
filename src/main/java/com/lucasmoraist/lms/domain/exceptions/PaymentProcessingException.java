package com.lucasmoraist.lms.domain.exceptions;

public class PaymentProcessingException extends RuntimeException {

    public PaymentProcessingException(String message) {
        super(message);
    }

}
