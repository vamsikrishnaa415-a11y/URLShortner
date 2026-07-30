package com.example.urlshortener.orchestratorservice.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {
    private final HttpStatus status;

    public ServiceException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ServiceException(String message, Throwable cause) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

    public ServiceException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}