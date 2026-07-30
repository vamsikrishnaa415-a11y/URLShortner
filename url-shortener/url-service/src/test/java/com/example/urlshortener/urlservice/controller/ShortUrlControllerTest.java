package com.example.urlshortener.urlservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.net.URI;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.urlshortener.urlservice.dto.ShortUrlResponseDto;
import com.example.urlshortener.urlservice.service.ShortUrlService;

@WebMvcTest(controllers = ShortUrlController.class)
class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShortUrlService shortUrlService;

    @Test
    void shouldCreateShortUrl() throws Exception {
        ShortUrlResponseDto response = new ShortUrlResponseDto(
                1L,
                "https://example.com",
                "abc12345",
                null,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                true,
                0L);

        when(shortUrlService.createShortUrl(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"https://example.com\",\"expiryDate\":\"2030-01-01T00:00:00Z\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/urls/abc12345"))
                .andExpect(jsonPath("$.shortCode").value("abc12345"));
    }

    @Test
    void shouldGetShortUrlByCode() throws Exception {
        ShortUrlResponseDto response = new ShortUrlResponseDto(
                2L,
                "https://example.org",
                "xyz12345",
                "promo",
                Instant.now(),
                null,
                true,
                4L);

        when(shortUrlService.getByShortCode("xyz12345")).thenReturn(response);

        mockMvc.perform(get("/api/v1/urls/xyz12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.customAlias").value("promo"));
    }

    @Test
    void shouldUpdateShortUrl() throws Exception {
        ShortUrlResponseDto response = new ShortUrlResponseDto(
                3L,
                "https://updated.example",
                "updated01",
                "updated01",
                Instant.now(),
                Instant.now().plusSeconds(7200),
                false,
                0L);

        when(shortUrlService.updateShortUrl(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/urls/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"https://updated.example\",\"customAlias\":\"updated01\",\"expiryDate\":\"2030-01-02T00:00:00Z\",\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("updated01"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void shouldDeleteShortUrl() throws Exception {
        doNothing().when(shortUrlService).deleteShortUrl(9L);

        mockMvc.perform(delete("/api/v1/urls/9"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRedirectByShortCode() throws Exception {
        when(shortUrlService.resolveRedirect("abc12345")).thenReturn(URI.create("https://example.com/page"));

        mockMvc.perform(get("/r/abc12345"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/page"));
    }
}