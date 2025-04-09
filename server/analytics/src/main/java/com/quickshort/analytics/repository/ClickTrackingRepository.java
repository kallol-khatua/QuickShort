package com.quickshort.analytics.repository;

import com.quickshort.analytics.dto.GenericStat;
import com.quickshort.analytics.dto.UrlClickStats;
import com.quickshort.analytics.model.ClickTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClickTrackingRepository extends JpaRepository<ClickTracking, UUID> {
    @Query(value = """
                SELECT s.short_code AS shortCode, s.original_url AS originalUrl, COUNT(c.id) AS totalClicks
                FROM short_urls s
                JOIN click_tracking c ON s.id = c.short_url_id
                WHERE s.workspace_id = :workspaceId
                AND c.created_at BETWEEN :fromDate AND :toDate
                GROUP BY s.short_code, s.original_url
            """, nativeQuery = true)
    List<UrlClickStats> getUrlClickStats(
            @Param("workspaceId") UUID workspaceId,
            @Param("fromDate") LocalDateTime from,
            @Param("toDate") LocalDateTime to
    );

    @Query(value = """
                SELECT c.device AS label, COUNT(*) AS total
                FROM short_urls s
                JOIN click_tracking c ON s.id = c.short_url_id
                WHERE s.workspace_id = :workspaceId
                AND c.created_at BETWEEN :fromDate AND :toDate
                GROUP BY c.device
            """, nativeQuery = true)
    List<GenericStat> getDeviceStats(
            @Param("workspaceId") UUID workspaceId,
            @Param("fromDate") LocalDateTime from,
            @Param("toDate") LocalDateTime to
    );

    @Query(value = """
                SELECT c.os AS label, COUNT(*) AS total
                FROM short_urls s
                JOIN click_tracking c ON s.id = c.short_url_id
                WHERE s.workspace_id = :workspaceId
                AND c.created_at BETWEEN :fromDate AND :toDate
                GROUP BY c.os
            """, nativeQuery = true)
    List<GenericStat> getOsStats(
            @Param("workspaceId") UUID workspaceId,
            @Param("fromDate") LocalDateTime from,
            @Param("toDate") LocalDateTime to
    );

    @Query(value = """
                SELECT c.browser AS label, COUNT(*) AS total
                FROM short_urls s
                JOIN click_tracking c ON s.id = c.short_url_id
                WHERE s.workspace_id = :workspaceId
                AND c.created_at BETWEEN :fromDate AND :toDate
                GROUP BY c.browser
            """, nativeQuery = true)
    List<GenericStat> getBrowserStats(
            @Param("workspaceId") UUID workspaceId,
            @Param("fromDate") LocalDateTime from,
            @Param("toDate") LocalDateTime to
    );
}
