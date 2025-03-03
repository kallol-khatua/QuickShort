package com.quickshort.analytics.service.impl;

import com.quickshort.analytics.dto.ShortUrlDto;
import com.quickshort.analytics.mapper.ShortUrlMapper;
import com.quickshort.analytics.model.ClickTracking;
import com.quickshort.analytics.model.ShortUrl;
import com.quickshort.analytics.repository.ClickTrackingRepository;
import com.quickshort.analytics.repository.ShortUrlRepository;
import com.quickshort.analytics.service.ShortUrlService;
import com.quickshort.analytics.service.UserAgent.UserAgentService;
import com.quickshort.analytics.service.redis.RedisShortUrlService;
import com.quickshort.common.enums.ShortUrlStatus;
import com.quickshort.common.exception.BadRequestException;
import com.quickshort.common.exception.FieldError;
import com.quickshort.common.exception.InternalServerErrorException;
import com.quickshort.common.payload.ShortUrlPayload;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlServiceImpl.class);

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Autowired
    private RedisShortUrlService redisShortUrlService;

    @Autowired
    private UserAgentService userAgentService;

    @Autowired
    private ClickTrackingRepository clickTrackingRepository;

    @Transactional
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


    @Transactional
    @Override
    public ShortUrlDto getOriginalUrl(HttpServletRequest request, String shortCode) {
        try {
            ShortUrl urlInCache = redisShortUrlService.findInCache(shortCode);
            ShortUrl shortUrl = null;

            if (urlInCache == null) {
                LOGGER.info("Short url not found in cache -> {}", shortCode);
                Optional<ShortUrl> existingUrl = shortUrlRepository.findByShortCode(shortCode);

                if (existingUrl.isEmpty()) {
                    List<FieldError> errors = new ArrayList<>();
                    errors.add(new FieldError("No url found", "short_code"));
                    throw new BadRequestException("Invalid Data Provided", "No url found", errors);
                }

                shortUrl = existingUrl.get();
            } else {
                LOGGER.info("Short url found in cache -> {}", shortCode);
                shortUrl = urlInCache;
            }


            // If status is not equal to ACTIVE then throw error
            if (shortUrl.getStatus() != ShortUrlStatus.ACTIVE) {
                List<FieldError> errors = new ArrayList<>();
                errors.add(new FieldError("Url is not active", "short_code"));
                throw new BadRequestException("Invalid Data Provided", "Url is not active", errors);
            }


            // If url not present in cache then cache the url
            if (urlInCache == null) {
                redisShortUrlService.addToCache(shortCode, shortUrl);
                LOGGER.info("Short url is cached -> {}", shortCode);
            }


            ClickTracking clickTracking = userAgentService.getClientInfo(request);
            clickTracking.setShortUrl(shortUrl);
            ClickTracking savedTracking = clickTrackingRepository.save(clickTracking);
            LOGGER.info("Tracking saved -> {}", shortCode);


            return ShortUrlMapper.mapToShortUrlDto(shortUrl);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during short url creation", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }
}
