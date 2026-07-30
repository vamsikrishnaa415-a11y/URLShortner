package com.example.urlshortener.analyticsservice.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.urlshortener.analyticsservice.dto.AnalyticsSummaryDto;
import com.example.urlshortener.analyticsservice.dto.ClickAnalyticsRequestDto;
import com.example.urlshortener.analyticsservice.dto.ClickAnalyticsResponseDto;
import com.example.urlshortener.analyticsservice.dto.DailyAnalyticsDto;
import com.example.urlshortener.analyticsservice.dto.TopAnalyticsDto;
import com.example.urlshortener.analyticsservice.entity.ClickAnalytics;
import com.example.urlshortener.analyticsservice.exception.BadRequestException;
import com.example.urlshortener.analyticsservice.exception.ResourceNotFoundException;
import com.example.urlshortener.analyticsservice.mapper.ClickAnalyticsMapper;
import com.example.urlshortener.analyticsservice.repository.ClickAnalyticsRepository;

@Service
public class AnalyticsService {

    private static final int DEFAULT_TOP_LIMIT = 10;
    private static final int MAX_TOP_LIMIT = 100;

    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final ClickAnalyticsMapper clickAnalyticsMapper;

    public AnalyticsService(ClickAnalyticsRepository clickAnalyticsRepository, ClickAnalyticsMapper clickAnalyticsMapper) {
        this.clickAnalyticsRepository = clickAnalyticsRepository;
        this.clickAnalyticsMapper = clickAnalyticsMapper;
    }

    @Transactional
    public ClickAnalyticsResponseDto storeRedirect(ClickAnalyticsRequestDto request) {
        ClickAnalytics entity = clickAnalyticsMapper.toEntity(request);
        ClickAnalytics saved = clickAnalyticsRepository.save(entity);
        return clickAnalyticsMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public AnalyticsSummaryDto getAnalyticsByShortCode(String shortCode) {
        List<ClickAnalytics> events = clickAnalyticsRepository.findByShortCode(shortCode);
        if (events.isEmpty()) {
            throw new ResourceNotFoundException("No analytics found for shortCode: " + shortCode);
        }
        List<ClickAnalyticsResponseDto> eventDtos = events.stream()
                .map(clickAnalyticsMapper::toResponseDto)
                .toList();
        return new AnalyticsSummaryDto(shortCode, events.size(), eventDtos);
    }

    @Transactional(readOnly = true)
    public List<TopAnalyticsDto> getTopAnalytics(Integer limit) {
        int safeLimit = limit == null ? DEFAULT_TOP_LIMIT : limit;
        if (safeLimit < 1 || safeLimit > MAX_TOP_LIMIT) {
            throw new BadRequestException("limit must be between 1 and " + MAX_TOP_LIMIT);
        }

        return clickAnalyticsRepository.findTopShortCodes(PageRequest.of(0, safeLimit)).stream()
                .map(item -> new TopAnalyticsDto(item.getShortCode(), item.getTotalClicks()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DailyAnalyticsDto> getDailyAnalytics(LocalDate from, LocalDate to) {
        LocalDate startDate = from == null ? LocalDate.now(ZoneOffset.UTC) : from;
        LocalDate endDate = to == null ? startDate : to;

        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("to date must be greater than or equal to from date");
        }

        Instant startInclusive = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInclusive = endDate.plusDays(1).atStartOfDay().minusNanos(1).toInstant(ZoneOffset.UTC);

        List<ClickAnalytics> events = clickAnalyticsRepository.findByClickedAtBetween(startInclusive, endInclusive);

        Map<LocalDate, Long> grouped = events.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getClickedAt().atOffset(ZoneOffset.UTC).toLocalDate(),
                        Collectors.counting()));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new DailyAnalyticsDto(entry.getKey(), entry.getValue()))
                .toList();
    }
}