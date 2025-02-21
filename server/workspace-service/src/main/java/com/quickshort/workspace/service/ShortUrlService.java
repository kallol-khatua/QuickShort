package com.quickshort.workspace.service;

import com.quickshort.workspace.dto.ShortUrlDto;
import com.quickshort.workspace.models.ShortUrl;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ShortUrlService {
    ShortUrlDto generateShortUrl(UUID workspaceId, ShortUrlDto shortUrlDto);

    Page<ShortUrl> getAllUrl(UUID workspaceId, int page, int size);
}
