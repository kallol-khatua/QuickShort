package com.quickshort.analytics.service;

import com.quickshort.analytics.dto.ShortUrlDto;
import com.quickshort.analytics.model.ShortUrl;
import com.quickshort.common.payload.ShortUrlPayload;
import jakarta.servlet.http.HttpServletRequest;

public interface ShortUrlService {
    ShortUrl saveShortUrl(ShortUrlPayload payload);

    ShortUrlDto getOriginalUrl(HttpServletRequest request, String shortCode);
}
