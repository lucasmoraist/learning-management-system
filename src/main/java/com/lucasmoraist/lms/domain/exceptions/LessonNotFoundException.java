package com.lucasmoraist.lms.domain.exceptions;

public class LessonNotFoundException extends RuntimeException {

    public LessonNotFoundException(String message) {
        super(message);
    }

}
