package com.example.urlshortener.orchestratorservice.exception;

import java.time.Instant;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.urlshortener.orchestratorservice.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), ex.getStatus(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage() == null ? "Internal server error" : ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, HttpServletRequest request) {
        String correlationId = request.getHeader("X-Correlation-Id");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = MDC.get("correlationId");
        }
        ErrorResponse payload = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                correlationId);
        return ResponseEntity.status(status).body(payload);
    }
}