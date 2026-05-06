package com.lucasmoraist.lms.domain.exceptions;

public class UniqueKeyDatabaseException extends RuntimeException {

    public UniqueKeyDatabaseException(String message) {
        super(message);
    }

}
