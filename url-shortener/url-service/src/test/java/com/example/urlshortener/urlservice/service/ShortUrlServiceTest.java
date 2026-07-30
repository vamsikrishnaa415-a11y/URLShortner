package com.example.urlshortener.urlservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.urlshortener.urlservice.dto.ShortUrlRequestDto;
import com.example.urlshortener.urlservice.dto.RedirectMetadataDto;
import com.example.urlshortener.urlservice.dto.ShortUrlUpdateRequestDto;
import com.example.urlshortener.urlservice.entity.ShortUrl;
import com.example.urlshortener.urlservice.exception.ConflictException;
import com.example.urlshortener.urlservice.exception.ResourceNotFoundException;
import com.example.urlshortener.urlservice.exception.UrlUnavailableException;
import com.example.urlshortener.urlservice.mapper.ShortUrlMapper;
import com.example.urlshortener.urlservice.repository.ShortUrlRepository;
import com.example.urlshortener.urlservice.util.ShortCodeGenerator;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private ShortUrlMapper shortUrlMapper;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @Mock
    private AnalyticsTrackingService analyticsTrackingService;

    @InjectMocks
    private ShortUrlService shortUrlService;

    private ShortUrl shortUrl;

    @BeforeEach
    void setUp() {
        shortUrl = new ShortUrl();
        shortUrl.setId(1L);
        shortUrl.setOriginalUrl("https://example.com");
        shortUrl.setShortCode("abc12345");
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setActive(true);
        shortUrl.setClickCount(0L);
    }

    @Test
    void shouldGenerateShortCodeWhenAliasNotProvided() {
        ShortUrlRequestDto request = new ShortUrlRequestDto("https://example.com", null, Instant.now().plusSeconds(3600));
        when(shortCodeGenerator.generate(8)).thenReturn("abc12345");
        when(shortUrlRepository.existsByShortCode("abc12345")).thenReturn(false);
        when(shortUrlRepository.save(any(ShortUrl.class))).thenReturn(shortUrl);

        shortUrlService.createShortUrl(request);

        verify(shortCodeGenerator).generate(8);
        verify(shortUrlRepository).save(any(ShortUrl.class));
    }

    @Test
    void shouldRejectDuplicateAlias() {
        ShortUrlRequestDto request = new ShortUrlRequestDto("https://example.com", "promo", Instant.now().plusSeconds(3600));
        when(shortUrlRepository.existsByCustomAlias("promo")).thenReturn(true);

        assertThatThrownBy(() -> shortUrlService.createShortUrl(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Custom alias already exists");

        verify(shortUrlRepository, never()).save(any(ShortUrl.class));
    }

    @Test
    void shouldIncrementClickCountAndReturnRedirectUri() {
        shortUrl.setClickCount(2L);
        when(shortUrlRepository.findByShortCodeAndActiveTrue("abc12345")).thenReturn(Optional.of(shortUrl));
        when(shortUrlRepository.save(any(ShortUrl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        URI uri = shortUrlService.resolveRedirect("abc12345", new RedirectMetadataDto("127.0.0.1", "Mozilla", null));

        assertThat(uri).isEqualTo(URI.create("https://example.com"));
        assertThat(shortUrl.getClickCount()).isEqualTo(3L);
        verify(shortUrlRepository).save(shortUrl);
        verify(analyticsTrackingService).trackRedirect(any(), any());
    }

    @Test
    void shouldRejectExpiredUrlDuringRedirect() {
        shortUrl.setExpiryDate(Instant.now().minusSeconds(60));
        when(shortUrlRepository.findByShortCodeAndActiveTrue("abc12345")).thenReturn(Optional.of(shortUrl));

        assertThatThrownBy(() -> shortUrlService.resolveRedirect("abc12345", null))
                .isInstanceOf(UrlUnavailableException.class)
                .hasMessageContaining("expired");

        verify(analyticsTrackingService, never()).trackRedirect(any(), any());
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingMissingRecord() {
        ShortUrlUpdateRequestDto request = new ShortUrlUpdateRequestDto(
                "https://new.example.com", null, Instant.now().plusSeconds(3600), true);
        when(shortUrlRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shortUrlService.updateShortUrl(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("id: 99");
    }
}