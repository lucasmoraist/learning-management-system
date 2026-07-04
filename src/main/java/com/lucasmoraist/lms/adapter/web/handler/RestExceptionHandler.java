package com.lucasmoraist.lms.adapter.web.handler;

import com.lucasmoraist.lms.adapter.web.handler.model.DataValidationException;
import com.lucasmoraist.lms.adapter.web.handler.model.ExceptionDTO;
import com.lucasmoraist.lms.domain.exceptions.AuthenticationException;
import com.lucasmoraist.lms.domain.exceptions.CertificateException;
import com.lucasmoraist.lms.domain.exceptions.LessonNotFoundException;
import com.lucasmoraist.lms.domain.exceptions.PaymentFailedException;
import com.lucasmoraist.lms.domain.exceptions.PaymentProcessingException;
import com.lucasmoraist.lms.domain.exceptions.StorageException;
import com.lucasmoraist.lms.domain.exceptions.TokenException;
import com.lucasmoraist.lms.domain.exceptions.UniqueKeyDatabaseException;
import com.lucasmoraist.lms.domain.exceptions.VideoMetadataException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

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
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionDTO(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(CertificateException.class)
    protected ResponseEntity<ExceptionDTO> handleCertificateException(CertificateException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE));
    }

    @ExceptionHandler(TokenException.class)
    protected ResponseEntity<ExceptionDTO> handleTokenException(TokenException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ExceptionDTO> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class
    })
    protected ResponseEntity<ExceptionDTO> handleBadRequestException(Exception ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ExceptionDTO> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(LessonNotFoundException.class)
    protected ResponseEntity<ExceptionDTO> handleLessonNotFoundException(LessonNotFoundException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(UniqueKeyDatabaseException.class)
    protected ResponseEntity<ExceptionDTO> handleUniqueKeyDatabaseException(UniqueKeyDatabaseException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDTO(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(PaymentProcessingException.class)
    protected ResponseEntity<ExceptionDTO> handlePaymentProcessingException(PaymentProcessingException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(PaymentFailedException.class)
    protected ResponseEntity<ExceptionDTO> handlePaymentFailedException(PaymentFailedException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ExceptionDTO> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ExceptionDTO("Uploaded file exceeds the maximum allowed size of 50MB", HttpStatus.PAYLOAD_TOO_LARGE));
    }

    @ExceptionHandler(MultipartException.class)
    protected ResponseEntity<ExceptionDTO> handleMultipartException(MultipartException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(VideoMetadataException.class)
    protected ResponseEntity<ExceptionDTO> handleVideoMetadataException(VideoMetadataException ex) {
        log.warn("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @ExceptionHandler(StorageException.class)
    protected ResponseEntity<ExceptionDTO> handleStorageException(StorageException ex) {
        log.error("Message: {} - ", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ExceptionDTO(ex.getMessage(), HttpStatus.BAD_GATEWAY));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Void> handleGenericException(Exception ex) {
        log.error("Exception Class: [{}] - Message: [{}]", ex.getClass(), ex.getMessage(), ex);
        return ResponseEntity.internalServerError().build();
    }

}
