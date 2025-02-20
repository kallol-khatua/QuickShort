package com.quickshort.analytics.service.impl;

import com.quickshort.analytics.model.ShortUrl;
import com.quickshort.analytics.repository.ShortUrlRepository;
import com.quickshort.analytics.service.ShortUrlService;
import com.quickshort.analytics.service.redis.RedisShortUrlService;
import com.quickshort.common.payload.ShortUrlPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlServiceImpl.class);

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Autowired
    private RedisShortUrlService redisShortUrlService;

    @Override
    public ShortUrl saveShortUrl(ShortUrlPayload payload) {
        try {
            ShortUrl newUrl = getNewUrl(payload);

            ShortUrl savedUrl = shortUrlRepository.save(newUrl);

            LOGGER.info("Short url saved -> {}", savedUrl);

            // Add to cache
            redisShortUrlService.addToCache(savedUrl.getShortCode(), savedUrl);
            LOGGER.info("Short url is added to cache");

            return savedUrl;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during short url creation", e);
            return null;
        }
    }

    private ShortUrl getNewUrl(ShortUrlPayload payload) {
        ShortUrl newUrl = new ShortUrl();

        newUrl.setId(payload.getId());
        newUrl.setWorkspaceId(payload.getWorkspaceId());
        newUrl.setWorkspaceMemberID(payload.getWorkspaceMemberID());
        newUrl.setOriginalUrl(payload.getOriginalUrl());
        newUrl.setShortCode(payload.getShortCode());
        newUrl.setExpiresAt(payload.getExpiresAt());
        newUrl.setActive(payload.isActive());
        newUrl.setStatus(payload.getStatus());
        newUrl.setCreatedAt(payload.getCreatedAt());
        newUrl.setUpdatedAt(payload.getUpdatedAt());

        return newUrl;
    }
}
