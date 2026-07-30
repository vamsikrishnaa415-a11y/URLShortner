package com.example.urlshortener.urlservice.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ServiceException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}