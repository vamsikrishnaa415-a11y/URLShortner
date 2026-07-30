package com.example.urlshortener.urlservice.controller;

import java.net.URI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.urlshortener.urlservice.dto.ErrorResponse;
import com.example.urlshortener.urlservice.dto.ShortUrlRequestDto;
import com.example.urlshortener.urlservice.dto.ShortUrlResponseDto;
import com.example.urlshortener.urlservice.dto.ShortUrlUpdateRequestDto;
import com.example.urlshortener.urlservice.service.ShortUrlService;

@Validated
@RestController
@Tag(name = "URL Service", description = "Operations for creating and resolving short URLs")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @Operation(summary = "Create short URL")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Short URL created"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate alias", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/api/v1/urls")
    public ResponseEntity<ShortUrlResponseDto> createShortUrl(@Valid @RequestBody ShortUrlRequestDto request) {
        ShortUrlResponseDto response = shortUrlService.createShortUrl(request);
        URI location = URI.create("/api/v1/urls/" + response.shortCode());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get short URL details by short code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Short URL found"),
            @ApiResponse(responseCode = "404", description = "Short URL not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/api/v1/urls/{shortCode}")
    public ResponseEntity<ShortUrlResponseDto> getByShortCode(
            @PathVariable @NotBlank @Size(min = 4, max = 64) String shortCode) {
        return ResponseEntity.ok(shortUrlService.getByShortCode(shortCode));
    }

    @Operation(summary = "Update short URL by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Short URL updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Short URL not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate alias", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/api/v1/urls/{id}")
    public ResponseEntity<ShortUrlResponseDto> updateShortUrl(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ShortUrlUpdateRequestDto request) {
        return ResponseEntity.ok(shortUrlService.updateShortUrl(id, request));
    }

    @Operation(summary = "Delete short URL by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Short URL deleted"),
            @ApiResponse(responseCode = "404", description = "Short URL not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/api/v1/urls/{id}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable @Positive Long id) {
        shortUrlService.deleteShortUrl(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Redirect to original URL by short code")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirected"),
            @ApiResponse(responseCode = "404", description = "Short URL not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "410", description = "Short URL expired or disabled", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable @NotBlank @Size(min = 4, max = 64) String shortCode) {
        URI redirectTo = shortUrlService.resolveRedirect(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectTo.toString())
                .build();
    }
}