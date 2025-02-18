package com.quickshort.workspace.mapper;

import com.quickshort.workspace.dto.ShortUrlDto;
import com.quickshort.workspace.models.ShortUrl;

public class ShortUrlMapper {
    public static ShortUrl mapToShortURL(ShortUrlDto shortUrlDto) {
        ShortUrl shortUrl = new ShortUrl();

        shortUrl.setId(shortUrlDto.getId());
        shortUrl.setOriginalUrl(shortUrlDto.getOriginalUrl());
        shortUrl.setShortCode(shortUrlDto.getShortCode());

        return shortUrl;
    }

    public static ShortUrlDto mapToShortURLDto(ShortUrl shortUrl) {
        ShortUrlDto shortUrlDto = new ShortUrlDto();

        shortUrlDto.setId(shortUrl.getId());
        shortUrlDto.setWorkspaceId(shortUrl.getWorkspaceId().getId());
        shortUrlDto.setWorkspaceMemberID(shortUrl.getWorkspaceMemberID().getId());
        shortUrlDto.setOriginalUrl(shortUrl.getOriginalUrl());
        shortUrlDto.setShortCode(shortUrl.getShortCode());
        shortUrlDto.setExpiresAt(shortUrl.getExpiresAt());
        shortUrlDto.setActive(shortUrl.isActive());
        shortUrlDto.setStatus(shortUrl.getStatus());

        return shortUrlDto;
    }
}
