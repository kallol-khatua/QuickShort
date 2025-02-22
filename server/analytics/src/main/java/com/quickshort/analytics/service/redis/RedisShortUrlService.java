package com.quickshort.analytics.service.redis;

import com.quickshort.analytics.model.ShortUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Service
public class RedisShortUrlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisShortUrlService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    String cacheKey = "short_url:";

    public ShortUrl findInCache(String shortCode) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object cachedData = valueOperations.get(cacheKey + shortCode);


        if (cachedData == null) {
            return null;
        }

        return objectMapper.convertValue(cachedData, ShortUrl.class);
    }

    public void addToCache(String shortCode, ShortUrl savedUrl) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // Cache result with TTL (e.g., 10 minutes)
        valueOperations.set(cacheKey + shortCode, savedUrl, 10, TimeUnit.MINUTES);
    }
}
