package com.quickshort.workspace.service;

import com.quickshort.workspace.dto.ShortUrlDto;

import java.util.UUID;

public interface ShortUrlService {
    ShortUrlDto generateShortUrl(UUID workspaceId, ShortUrlDto shortUrlDto);
}
