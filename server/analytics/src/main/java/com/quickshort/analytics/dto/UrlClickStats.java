package com.quickshort.analytics.dto;

public interface UrlClickStats {
    String getShortCode();
    String getOriginalUrl();
    Long getTotalClicks();
}
