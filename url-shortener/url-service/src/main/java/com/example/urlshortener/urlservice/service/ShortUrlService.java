package com.example.urlshortener.urlservice.service;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.urlshortener.urlservice.dto.ShortUrlRequestDto;
import com.example.urlshortener.urlservice.dto.RedirectMetadataDto;
import com.example.urlshortener.urlservice.dto.ShortUrlResponseDto;
import com.example.urlshortener.urlservice.dto.ShortUrlUpdateRequestDto;
import com.example.urlshortener.urlservice.entity.ShortUrl;
import com.example.urlshortener.urlservice.exception.BadRequestException;
import com.example.urlshortener.urlservice.exception.ConflictException;
import com.example.urlshortener.urlservice.exception.ResourceNotFoundException;
import com.example.urlshortener.urlservice.exception.ServiceException;
import com.example.urlshortener.urlservice.exception.UrlUnavailableException;
import com.example.urlshortener.urlservice.mapper.ShortUrlMapper;
import com.example.urlshortener.urlservice.repository.ShortUrlRepository;
import com.example.urlshortener.urlservice.util.ShortCodeGenerator;

@Service
public class ShortUrlService {

    private static final int SHORT_CODE_LENGTH = 8;
    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlMapper shortUrlMapper;
    private final ShortCodeGenerator shortCodeGenerator;
    private final AnalyticsTrackingService analyticsTrackingService;

    public ShortUrlService(
            ShortUrlRepository shortUrlRepository,
            ShortUrlMapper shortUrlMapper,
            ShortCodeGenerator shortCodeGenerator,
            AnalyticsTrackingService analyticsTrackingService) {
        this.shortUrlRepository = shortUrlRepository;
        this.shortUrlMapper = shortUrlMapper;
        this.shortCodeGenerator = shortCodeGenerator;
        this.analyticsTrackingService = analyticsTrackingService;
    }

    @Transactional
    public ShortUrlResponseDto createShortUrl(ShortUrlRequestDto request) {
        validateExpiryDate(request.expiryDate());

        String normalizedAlias = normalizeAlias(request.customAlias());
        String shortCode = normalizedAlias != null ? normalizedAlias : generateUniqueShortCode();

        if (normalizedAlias != null && shortUrlRepository.existsByCustomAlias(normalizedAlias)) {
            throw new ConflictException("Custom alias already exists: " + normalizedAlias);
        }

        if (shortUrlRepository.existsByShortCode(shortCode)) {
            throw new ConflictException("Short code already exists: " + shortCode);
        }

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(request.originalUrl());
        shortUrl.setShortCode(shortCode);
        shortUrl.setCustomAlias(normalizedAlias);
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setExpiryDate(request.expiryDate());
        shortUrl.setActive(true);
        shortUrl.setClickCount(0L);

        return shortUrlMapper.toResponseDto(shortUrlRepository.save(shortUrl));
    }

    @Transactional(readOnly = true)
    public ShortUrlResponseDto getByShortCode(String shortCode) {
        ShortUrl entity = findByCodeOrAlias(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found for code: " + shortCode));
        return shortUrlMapper.toResponseDto(entity);
    }

    @Transactional
    public ShortUrlResponseDto updateShortUrl(Long id, ShortUrlUpdateRequestDto request) {
        validateExpiryDate(request.expiryDate());

        ShortUrl existing = shortUrlRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found for id: " + id));

        String normalizedAlias = normalizeAlias(request.customAlias());
        if (normalizedAlias != null) {
            boolean aliasTaken = shortUrlRepository.findByCustomAlias(normalizedAlias)
                    .map(record -> !record.getId().equals(id))
                    .orElse(false);
            if (aliasTaken) {
                throw new ConflictException("Custom alias already exists: " + normalizedAlias);
            }
            existing.setCustomAlias(normalizedAlias);
            existing.setShortCode(normalizedAlias);
        }

        existing.setOriginalUrl(request.originalUrl());
        existing.setExpiryDate(request.expiryDate());
        existing.setActive(request.active());

        return shortUrlMapper.toResponseDto(shortUrlRepository.save(existing));
    }

    @Transactional
    public void deleteShortUrl(Long id) {
        if (!shortUrlRepository.existsById(id)) {
            throw new ResourceNotFoundException("Short URL not found for id: " + id);
        }
        shortUrlRepository.deleteById(id);
    }

    @Transactional
    public URI resolveRedirect(String shortCode) {
        return resolveRedirect(shortCode, null);
    }

    @Transactional
    public URI resolveRedirect(String shortCode, RedirectMetadataDto metadata) {
        ShortUrl entity = shortUrlRepository.findByShortCodeAndActiveTrue(shortCode)
                .or(() -> shortUrlRepository.findByCustomAliasAndActiveTrue(shortCode))
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found for code: " + shortCode));

        if (isExpired(entity.getExpiryDate())) {
            throw new UrlUnavailableException("Short URL has expired");
        }

        if (!Boolean.TRUE.equals(entity.getActive())) {
            throw new UrlUnavailableException("Short URL is disabled");
        }

        entity.setClickCount(entity.getClickCount() + 1);
        shortUrlRepository.save(entity);

        analyticsTrackingService.trackRedirect(entity.getShortCode(), metadata);

        return URI.create(entity.getOriginalUrl());
    }

    private Optional<ShortUrl> findByCodeOrAlias(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode)
                .or(() -> shortUrlRepository.findByCustomAlias(shortCode));
    }

    private String generateUniqueShortCode() {
        for (int i = 0; i < MAX_GENERATION_ATTEMPTS; i++) {
            String generated = shortCodeGenerator.generate(SHORT_CODE_LENGTH);
            if (!shortUrlRepository.existsByShortCode(generated)) {
                return generated;
            }
        }
        throw new ServiceException("Could not generate a unique short code");
    }

    private String normalizeAlias(String alias) {
        if (alias == null) {
            return null;
        }
        String trimmed = alias.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validateExpiryDate(Instant expiryDate) {
        if (expiryDate != null && !expiryDate.isAfter(Instant.now())) {
            throw new BadRequestException("expiryDate must be in the future");
        }
    }

    private boolean isExpired(Instant expiryDate) {
        return expiryDate != null && !expiryDate.isAfter(Instant.now());
    }
}