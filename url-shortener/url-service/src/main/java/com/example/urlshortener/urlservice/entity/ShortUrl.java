package com.example.urlshortener.urlservice.entity;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import com.example.urlshortener.urlservice.validation.ValidationPatterns;

@Entity
@Table(
        name = "short_urls",
        indexes = {
                @Index(name = "idx_short_urls_short_code", columnList = "short_code", unique = true),
                @Index(name = "idx_short_urls_custom_alias", columnList = "custom_alias", unique = true),
                @Index(name = "idx_short_urls_active_expiry", columnList = "active,expiry_date")
        })
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 2048)
    @Pattern(regexp = ValidationPatterns.URL_PATTERN)
    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @NotBlank
    @Size(min = 4, max = 32)
    @Pattern(regexp = "^[A-Za-z0-9_-]+$")
    @Column(name = "short_code", nullable = false, unique = true, length = 32)
    private String shortCode;

    @Size(max = 64)
    @Pattern(regexp = "^[A-Za-z0-9_-]*$")
    @Column(name = "custom_alias", unique = true, length = 64)
    private String customAlias;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @PositiveOrZero
    @Column(name = "click_count", nullable = false)
    private Long clickCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }
}