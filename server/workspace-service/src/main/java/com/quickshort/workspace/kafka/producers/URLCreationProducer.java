package com.quickshort.workspace.kafka.producers;

import com.quickshort.common.events.ShortUrlCreationEvent;
import com.quickshort.common.events.UserAccountCreationEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class URLCreationProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLCreationProducer.class);

    private final KafkaTemplate<String, ShortUrlCreationEvent> kafkaTemplate;
    private final NewTopic urlCreationTopic;

    public URLCreationProducer(KafkaTemplate<String, ShortUrlCreationEvent> kafkaTemplate, NewTopic urlCreationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.urlCreationTopic = urlCreationTopic;
    }

    public void sendURLCreationMessage(String key, ShortUrlCreationEvent event) {
        // Send event
        kafkaTemplate.send(urlCreationTopic.name(), key, event);

        LOGGER.info(String.format("Short URL Creation event sent -> %s", event.toString()));
    }
}
