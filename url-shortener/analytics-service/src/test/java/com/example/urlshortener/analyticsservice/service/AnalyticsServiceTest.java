package com.example.urlshortener.analyticsservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import com.example.urlshortener.analyticsservice.dto.AnalyticsSummaryDto;
import com.example.urlshortener.analyticsservice.dto.ClickAnalyticsRequestDto;
import com.example.urlshortener.analyticsservice.dto.ClickAnalyticsResponseDto;
import com.example.urlshortener.analyticsservice.dto.TopAnalyticsDto;
import com.example.urlshortener.analyticsservice.entity.ClickAnalytics;
import com.example.urlshortener.analyticsservice.exception.BadRequestException;
import com.example.urlshortener.analyticsservice.exception.ResourceNotFoundException;
import com.example.urlshortener.analyticsservice.mapper.ClickAnalyticsMapper;
import com.example.urlshortener.analyticsservice.repository.ClickAnalyticsRepository;
import com.example.urlshortener.analyticsservice.repository.TopAnalyticsProjection;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ClickAnalyticsRepository clickAnalyticsRepository;

    @Mock
    private ClickAnalyticsMapper clickAnalyticsMapper;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void shouldStoreRedirectEvent() {
        ClickAnalyticsRequestDto request = new ClickAnalyticsRequestDto(
                "abc123",
                Instant.now(),
                "10.0.0.1",
                "Chrome",
                "Desktop",
                "Windows",
                "https://ref.example");

        ClickAnalytics entity = new ClickAnalytics();
        entity.setShortCode("abc123");
        ClickAnalytics saved = new ClickAnalytics();
        saved.setId(10L);
        saved.setShortCode("abc123");

        ClickAnalyticsResponseDto response = new ClickAnalyticsResponseDto(
                10L,
                "abc123",
                request.clickedAt(),
                request.ipAddress(),
                request.browser(),
                request.device(),
                request.operatingSystem(),
                request.referrer());

        when(clickAnalyticsMapper.toEntity(request)).thenReturn(entity);
        when(clickAnalyticsRepository.save(entity)).thenReturn(saved);
        when(clickAnalyticsMapper.toResponseDto(saved)).thenReturn(response);

        ClickAnalyticsResponseDto result = analyticsService.storeRedirect(request);
        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    void shouldReturnSummaryByShortCode() {
        ClickAnalytics first = event("abc123", Instant.now().minusSeconds(60));
        ClickAnalytics second = event("abc123", Instant.now());

        when(clickAnalyticsRepository.findByShortCode("abc123")).thenReturn(List.of(first, second));
        when(clickAnalyticsMapper.toResponseDto(any())).thenAnswer(invocation -> {
            ClickAnalytics item = invocation.getArgument(0);
            return new ClickAnalyticsResponseDto(
                    item.getId(),
                    item.getShortCode(),
                    item.getClickedAt(),
                    item.getIpAddress(),
                    item.getBrowser(),
                    item.getDevice(),
                    item.getOperatingSystem(),
                    item.getReferrer());
        });

        AnalyticsSummaryDto summary = analyticsService.getAnalyticsByShortCode("abc123");
        assertThat(summary.totalClicks()).isEqualTo(2L);
        assertThat(summary.shortCode()).isEqualTo("abc123");
    }

    @Test
    void shouldThrowNotFoundWhenNoDataForShortCode() {
        when(clickAnalyticsRepository.findByShortCode("missing")).thenReturn(List.of());

        assertThatThrownBy(() -> analyticsService.getAnalyticsByShortCode("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnTopAnalytics() {
        TopAnalyticsProjection projection = new TopAnalyticsProjection() {
            @Override
            public String getShortCode() {
                return "hot001";
            }

            @Override
            public long getTotalClicks() {
                return 99L;
            }
        };

        when(clickAnalyticsRepository.findTopShortCodes(any(Pageable.class))).thenReturn(List.of(projection));

        List<TopAnalyticsDto> result = analyticsService.getTopAnalytics(5);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).shortCode()).isEqualTo("hot001");
    }

    @Test
    void shouldRejectInvalidTopLimit() {
        assertThatThrownBy(() -> analyticsService.getTopAnalytics(0))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("limit");
    }

    @Test
    void shouldAggregateDailyAnalytics() {
        Instant dayOne = LocalDate.of(2026, 7, 30).atStartOfDay().toInstant(ZoneOffset.UTC).plusSeconds(120);
        Instant dayTwo = LocalDate.of(2026, 7, 31).atStartOfDay().toInstant(ZoneOffset.UTC).plusSeconds(120);

        when(clickAnalyticsRepository.findByClickedAtBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(event("a", dayOne), event("b", dayTwo), event("c", dayTwo)));

        var result = analyticsService.getDailyAnalytics(LocalDate.of(2026, 7, 30), LocalDate.of(2026, 7, 31));
        assertThat(result).hasSize(2);
        assertThat(result.get(0).totalClicks()).isEqualTo(1L);
        assertThat(result.get(1).totalClicks()).isEqualTo(2L);
    }

    private ClickAnalytics event(String shortCode, Instant clickedAt) {
        ClickAnalytics entity = new ClickAnalytics();
        entity.setShortCode(shortCode);
        entity.setClickedAt(clickedAt);
        entity.setIpAddress("127.0.0.1");
        entity.setBrowser("Chrome");
        entity.setDevice("Desktop");
        entity.setOperatingSystem("Windows");
        entity.setReferrer("https://ref.example");
        return entity;
    }
}