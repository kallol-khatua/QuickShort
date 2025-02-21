package com.quickshort.analytics.kafka.consumer;

import com.quickshort.analytics.model.ShortUrl;
import com.quickshort.analytics.service.ShortUrlService;
import com.quickshort.common.events.ShortUrlCreationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UrlCreationTopicConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlCreationTopicConsumer.class);

    @Autowired
    private ShortUrlService shortUrlService;

    @KafkaListener(topics = "url-creation")
    public void urlCreation(ShortUrlCreationEvent event) {
        try {
            LOGGER.info("Short url creation event received in analytics service -> {}", event);

            ShortUrl savedUrl = shortUrlService.saveShortUrl(event.getShortUrlPayload());
        } catch (Exception e) {
            LOGGER.error("Error processing Short url creation event in analytics service: {}", e.getMessage(), e);
        }
    }
}
