package com.example.urlshortener.analyticsservice.validation;

public final class ValidationPatterns {
    public static final String URL_PATTERN = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";

    private ValidationPatterns() {
    }
}