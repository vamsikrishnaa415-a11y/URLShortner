package com.example.urlshortener.urlservice.exception;

import org.springframework.http.HttpStatus;

public class UrlUnavailableException extends ServiceException {
    public UrlUnavailableException(String message) {
        super(message, HttpStatus.GONE);
    }
}