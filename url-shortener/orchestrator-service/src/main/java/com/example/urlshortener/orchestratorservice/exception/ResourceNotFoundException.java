package com.example.urlshortener.orchestratorservice.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ServiceException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}