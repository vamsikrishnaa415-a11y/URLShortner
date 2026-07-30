package com.example.urlshortener.analyticsservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import com.example.urlshortener.analyticsservice.entity.ClickAnalytics;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class ClickAnalyticsRepositoryTest {

    @Autowired
    private ClickAnalyticsRepository clickAnalyticsRepository;

    @Test
    void shouldSaveAndQueryByShortCode() {
        ClickAnalytics first = createEvent("abc123", Instant.now().minusSeconds(10));
        ClickAnalytics second = createEvent("abc123", Instant.now());

        clickAnalyticsRepository.save(first);
        clickAnalyticsRepository.save(second);

        List<ClickAnalytics> records = clickAnalyticsRepository.findByShortCode("abc123");
        assertThat(records).hasSize(2);
        assertThat(clickAnalyticsRepository.countByShortCode("abc123")).isEqualTo(2);
    }

    @Test
    void shouldQueryByClickedAtRange() {
        Instant now = Instant.now();
        clickAnalyticsRepository.save(createEvent("codeA", now.minusSeconds(120)));
        clickAnalyticsRepository.save(createEvent("codeB", now.minusSeconds(60)));
        clickAnalyticsRepository.save(createEvent("codeC", now.plusSeconds(60)));

        List<ClickAnalytics> records = clickAnalyticsRepository.findByClickedAtBetween(now.minusSeconds(90), now.plusSeconds(10));
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getShortCode()).isEqualTo("codeB");
    }

    private ClickAnalytics createEvent(String shortCode, Instant clickedAt) {
        ClickAnalytics analytics = new ClickAnalytics();
        analytics.setShortCode(shortCode);
        analytics.setClickedAt(clickedAt);
        analytics.setIpAddress("127.0.0.1");
        analytics.setBrowser("Chrome");
        analytics.setDevice("Desktop");
        analytics.setOperatingSystem("Windows");
        analytics.setReferrer("https://search.example");
        return analytics;
    }
}