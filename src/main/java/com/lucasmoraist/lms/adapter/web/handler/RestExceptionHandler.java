package com.lucasmoraist.lms.adapter.web.handler;

import com.lucasmoraist.lms.adapter.web.handler.model.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<List<DataValidationException>> handleDataRequestException(MethodArgumentNotValidException ex) {
        List<DataValidationException> errors = ex.getFieldErrors()
                .stream()
                .map(DataValidationException::new)
                .toList();
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(errors);
    }

}
