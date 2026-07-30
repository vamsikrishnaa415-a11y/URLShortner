package com.example.urlshortener.urlservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.urlshortener.urlservice.client.AnalyticsServiceClient;
import com.example.urlshortener.urlservice.dto.RedirectMetadataDto;

@ExtendWith(MockitoExtension.class)
class AnalyticsTrackingServiceTest {

    @Mock
    private AnalyticsServiceClient analyticsServiceClient;

    @InjectMocks
    private AnalyticsTrackingService analyticsTrackingService;

    @Test
    void shouldCallAnalyticsServiceClient() {
        RedirectMetadataDto metadata = new RedirectMetadataDto(
                "10.10.10.1",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/125.0",
                "https://ref.example");

        analyticsTrackingService.trackRedirect("abc12345", metadata);

        verify(analyticsServiceClient).storeRedirectEvent(any());
    }

    @Test
    void shouldFallbackSilentlyWhenAnalyticsClientFails() {
        doThrow(new RuntimeException("timeout")).when(analyticsServiceClient).storeRedirectEvent(any());

        analyticsTrackingService.trackRedirect("abc12345", new RedirectMetadataDto("1.1.1.1", "Mozilla", null));

        verify(analyticsServiceClient).storeRedirectEvent(any());
        verifyNoMoreInteractions(analyticsServiceClient);
    }
}