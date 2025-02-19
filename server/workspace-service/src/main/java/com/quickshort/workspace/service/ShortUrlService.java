package com.quickshort.workspace.service;

import com.quickshort.workspace.dto.ShortUrlDto;

import java.util.List;
import java.util.UUID;

public interface ShortUrlService {
    ShortUrlDto generateShortUrl(UUID workspaceId, ShortUrlDto shortUrlDto);

    List<ShortUrlDto> getAllUrl(UUID workspaceId);
}
