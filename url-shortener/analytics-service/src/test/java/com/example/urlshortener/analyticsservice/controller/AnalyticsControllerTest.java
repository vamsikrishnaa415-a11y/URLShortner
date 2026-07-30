package com.example.urlshortener.analyticsservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.urlshortener.analyticsservice.dto.AnalyticsSummaryDto;
import com.example.urlshortener.analyticsservice.dto.ClickAnalyticsResponseDto;
import com.example.urlshortener.analyticsservice.dto.DailyAnalyticsDto;
import com.example.urlshortener.analyticsservice.dto.TopAnalyticsDto;
import com.example.urlshortener.analyticsservice.service.AnalyticsService;

@WebMvcTest(controllers = AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    void shouldStoreRedirectEvent() throws Exception {
        ClickAnalyticsResponseDto response = new ClickAnalyticsResponseDto(
                1L,
                "abc123",
                Instant.parse("2026-07-30T00:00:00Z"),
                "10.0.0.1",
                "Chrome",
                "Desktop",
                "Windows",
                "https://ref.example");

        when(analyticsService.storeRedirect(any())).thenReturn(response);

        mockMvc.perform(post("/analytics/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shortCode\":\"abc123\",\"clickedAt\":\"2026-07-30T00:00:00Z\",\"ipAddress\":\"10.0.0.1\",\"browser\":\"Chrome\",\"device\":\"Desktop\",\"operatingSystem\":\"Windows\",\"referrer\":\"https://ref.example\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("abc123"));
    }

    @Test
    void shouldReturnAnalyticsByShortCode() throws Exception {
        ClickAnalyticsResponseDto event = new ClickAnalyticsResponseDto(
                1L,
                "abc123",
                Instant.parse("2026-07-30T00:00:00Z"),
                "10.0.0.1",
                "Chrome",
                "Desktop",
                "Windows",
                "https://ref.example");

        AnalyticsSummaryDto summary = new AnalyticsSummaryDto("abc123", 1L, List.of(event));
        when(analyticsService.getAnalyticsByShortCode("abc123")).thenReturn(summary);

        mockMvc.perform(get("/analytics/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.totalClicks").value(1));
    }

    @Test
    void shouldReturnTopAnalytics() throws Exception {
        when(analyticsService.getTopAnalytics(2)).thenReturn(List.of(
                new TopAnalyticsDto("top001", 10L),
                new TopAnalyticsDto("top002", 8L)));

        mockMvc.perform(get("/analytics/top").param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shortCode").value("top001"));
    }

    @Test
    void shouldReturnDailyAnalytics() throws Exception {
        when(analyticsService.getDailyAnalytics(LocalDate.of(2026, 7, 29), LocalDate.of(2026, 7, 30)))
                .thenReturn(List.of(
                        new DailyAnalyticsDto(LocalDate.of(2026, 7, 29), 5L),
                        new DailyAnalyticsDto(LocalDate.of(2026, 7, 30), 7L)));

        mockMvc.perform(get("/analytics/daily")
                        .param("from", "2026-07-29")
                        .param("to", "2026-07-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].totalClicks").value(7));
    }
}