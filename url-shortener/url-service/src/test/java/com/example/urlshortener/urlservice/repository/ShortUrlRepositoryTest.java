package com.example.urlshortener.urlservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import com.example.urlshortener.urlservice.entity.ShortUrl;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class ShortUrlRepositoryTest {

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Test
    void shouldSaveAndFindByShortCodeWhenActive() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl("https://example.com/path");
        shortUrl.setShortCode("abc123");
        shortUrl.setCustomAlias("promo-link");
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setExpiryDate(Instant.now().plusSeconds(3600));
        shortUrl.setActive(true);
        shortUrl.setClickCount(0L);

        shortUrlRepository.save(shortUrl);

        Optional<ShortUrl> found = shortUrlRepository.findByShortCodeAndActiveTrue("abc123");
        assertThat(found).isPresent();
        assertThat(found.get().getOriginalUrl()).isEqualTo("https://example.com/path");
    }

    @Test
    void shouldReturnExistenceForShortCodeAndCustomAlias() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl("https://example.com/offer");
        shortUrl.setShortCode("offer42");
        shortUrl.setCustomAlias("offer-alias");
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setActive(true);
        shortUrl.setClickCount(0L);

        shortUrlRepository.save(shortUrl);

        assertThat(shortUrlRepository.existsByShortCode("offer42")).isTrue();
        assertThat(shortUrlRepository.existsByCustomAlias("offer-alias")).isTrue();
    }
}