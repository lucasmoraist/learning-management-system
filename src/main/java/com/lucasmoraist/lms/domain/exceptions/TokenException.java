package com.lucasmoraist.lms.domain.exceptions;

public class TokenException extends RuntimeException {

    public TokenException(String message, Throwable ex) {
        super(message, ex);
    }

}
