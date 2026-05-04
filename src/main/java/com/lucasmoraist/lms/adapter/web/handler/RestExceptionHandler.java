package com.lucasmoraist.lms.adapter.web.handler;

import com.lucasmoraist.lms.adapter.web.handler.model.DataValidationException;
import com.lucasmoraist.lms.adapter.web.handler.model.ExceptionDTO;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.exceptions.CertificateException;
import com.lucasmoraist.lms.domain.exceptions.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ExceptionDTO> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(401).body(new ExceptionDTO(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(CertificateException.class)
    protected ResponseEntity<Void> handleCertificateException(CertificateException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(503).build();
    }

    @ExceptionHandler(TokenException.class)
    protected ResponseEntity<ExceptionDTO> handleTokenException(TokenException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(401).body(new ExceptionDTO(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Void> handleGenericException(Exception ex) {
        log.error("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().build();
    }

}
