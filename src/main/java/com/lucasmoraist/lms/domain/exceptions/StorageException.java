package com.lucasmoraist.lms.domain.exceptions;

public class StorageException extends RuntimeException {

    public StorageException(String message, Throwable ex) {
        super(message, ex);
    }

}
