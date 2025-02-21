package com.quickshort.analytics.service;

import com.quickshort.analytics.model.ShortUrl;
import com.quickshort.common.payload.ShortUrlPayload;

public interface ShortUrlService {
    ShortUrl saveShortUrl(ShortUrlPayload payload);
}
