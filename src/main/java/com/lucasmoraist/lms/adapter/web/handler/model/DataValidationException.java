package com.lucasmoraist.lms.adapter.web.handler.model;

import org.springframework.validation.FieldError;

public record DataValidationException(
        String label,
        String message
) {
    public DataValidationException(FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }

}
