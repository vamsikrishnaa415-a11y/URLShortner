package com.example.urlshortener.analyticsservice.controller;

import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.urlshortener.analyticsservice.dto.AnalyticsSummaryDto;
import com.example.urlshortener.analyticsservice.dto.ClickAnalyticsRequestDto;
import com.example.urlshortener.analyticsservice.dto.ClickAnalyticsResponseDto;
import com.example.urlshortener.analyticsservice.dto.DailyAnalyticsDto;
import com.example.urlshortener.analyticsservice.dto.ErrorResponse;
import com.example.urlshortener.analyticsservice.dto.TopAnalyticsDto;
import com.example.urlshortener.analyticsservice.service.AnalyticsService;

@Validated
@RestController
@RequestMapping("/analytics")
@Tag(name = "Analytics Service", description = "Analytics APIs for redirect tracking and aggregation")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Operation(summary = "Store a redirect event")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event stored"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/events")
    public ResponseEntity<ClickAnalyticsResponseDto> storeRedirect(@Valid @RequestBody ClickAnalyticsRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(analyticsService.storeRedirect(request));
    }

    @Operation(summary = "Get analytics by short code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analytics found"),
            @ApiResponse(responseCode = "404", description = "Analytics not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{shortCode}")
    public ResponseEntity<AnalyticsSummaryDto> getByShortCode(
            @PathVariable @NotBlank @Size(max = 32) String shortCode) {
        return ResponseEntity.ok(analyticsService.getAnalyticsByShortCode(shortCode));
    }

    @Operation(summary = "Get top clicked short codes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top analytics returned"),
            @ApiResponse(responseCode = "400", description = "Invalid limit", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/top")
    public ResponseEntity<List<TopAnalyticsDto>> getTop(
            @Parameter(description = "Maximum items to return, default 10")
            @RequestParam(required = false) @Min(1) Integer limit) {
        return ResponseEntity.ok(analyticsService.getTopAnalytics(limit));
    }

    @Operation(summary = "Get daily analytics totals")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Daily analytics returned"),
            @ApiResponse(responseCode = "400", description = "Invalid date range", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/daily")
    public ResponseEntity<List<DailyAnalyticsDto>> getDaily(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(analyticsService.getDailyAnalytics(from, to));
    }
}