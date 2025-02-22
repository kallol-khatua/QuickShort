package com.quickshort.analytics.mapper;

import com.quickshort.analytics.dto.ShortUrlDto;
import com.quickshort.analytics.model.ShortUrl;

public class ShortUrlMapper {
    public static ShortUrlDto mapToShortUrlDto(ShortUrl shortUrl) {
        ShortUrlDto shortUrlDto = new ShortUrlDto();

        shortUrlDto.setId(shortUrl.getId());
        shortUrlDto.setOriginalUrl(shortUrl.getOriginalUrl());
        shortUrlDto.setShortCode(shortUrl.getShortCode());
        shortUrlDto.setExpiresAt(shortUrl.getExpiresAt());
        shortUrlDto.setStatus(shortUrl.getStatus());
        shortUrlDto.setActive(shortUrlDto.isActive());

        return shortUrlDto;
    }
}
