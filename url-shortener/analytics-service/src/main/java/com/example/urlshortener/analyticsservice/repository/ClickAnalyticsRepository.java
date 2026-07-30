package com.example.urlshortener.analyticsservice.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.urlshortener.analyticsservice.entity.ClickAnalytics;

@Repository
public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {

    List<ClickAnalytics> findByShortCode(String shortCode);

    long countByShortCode(String shortCode);

    List<ClickAnalytics> findByClickedAtBetween(Instant startTime, Instant endTime);

    @Query("select c.shortCode as shortCode, count(c.id) as totalClicks "
            + "from ClickAnalytics c group by c.shortCode order by count(c.id) desc")
    List<TopAnalyticsProjection> findTopShortCodes(Pageable pageable);
}