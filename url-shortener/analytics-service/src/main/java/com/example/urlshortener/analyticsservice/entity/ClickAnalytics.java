package com.example.urlshortener.analyticsservice.entity;

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
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name = "click_analytics",
        indexes = {
                @Index(name = "idx_click_analytics_short_code", columnList = "short_code"),
                @Index(name = "idx_click_analytics_clicked_at", columnList = "clicked_at"),
                @Index(name = "idx_click_analytics_short_code_clicked_at", columnList = "short_code,clicked_at")
        })
public class ClickAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 32)
    @Column(name = "short_code", nullable = false, length = 32)
    private String shortCode;

    @NotNull
    @Column(name = "clicked_at", nullable = false)
    private Instant clickedAt;

    @NotBlank
    @Size(max = 45)
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @NotBlank
    @Size(max = 128)
    @Column(name = "browser", nullable = false, length = 128)
    private String browser;

    @NotBlank
    @Size(max = 128)
    @Column(name = "device", nullable = false, length = 128)
    private String device;

    @NotBlank
    @Size(max = 128)
    @Column(name = "operating_system", nullable = false, length = 128)
    private String operatingSystem;

    @Size(max = 512)
    @Column(name = "referrer", length = 512)
    private String referrer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Instant getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(Instant clickedAt) {
        this.clickedAt = clickedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }
}