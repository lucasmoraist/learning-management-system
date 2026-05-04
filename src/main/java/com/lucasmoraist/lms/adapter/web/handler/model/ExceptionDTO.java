package com.lucasmoraist.lms.adapter.web.handler.model;

import org.springframework.http.HttpStatus;

public record ExceptionDTO(
        String message,
        HttpStatus status
) {

}
