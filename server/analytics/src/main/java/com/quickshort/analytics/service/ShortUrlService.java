package com.quickshort.analytics.service;

import com.quickshort.analytics.dto.ClickAnalyticsResponse;
import com.quickshort.analytics.dto.ShortUrlDto;
import com.quickshort.analytics.model.ShortUrl;
import com.quickshort.common.payload.ShortUrlPayload;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ShortUrlService {
    ShortUrl saveShortUrl(ShortUrlPayload payload);

    ShortUrlDto getOriginalUrl(HttpServletRequest request, String shortCode);

    ClickAnalyticsResponse getReport(UUID workspaceId, LocalDateTime fromDateAndTime, LocalDateTime toDateAndTime);
}
