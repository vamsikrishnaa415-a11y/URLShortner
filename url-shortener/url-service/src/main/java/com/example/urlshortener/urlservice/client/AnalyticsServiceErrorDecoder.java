package com.example.urlshortener.urlservice.client;

import feign.Response;
import feign.codec.ErrorDecoder;

public class AnalyticsServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return new RuntimeException("Analytics service call failed with status " + response.status() + " for " + methodKey,
                defaultDecoder.decode(methodKey, response));
    }
}