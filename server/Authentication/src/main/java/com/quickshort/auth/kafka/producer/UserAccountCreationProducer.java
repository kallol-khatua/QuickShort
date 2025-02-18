package com.quickshort.auth.kafka.producer;

import com.quickshort.common.events.UserAccountCreationEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserAccountCreationProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountCreationProducer.class);

    private final KafkaTemplate<String, UserAccountCreationEvent> kafkaTemplate;
    private final NewTopic userAccountCreationTopic;

    public UserAccountCreationProducer(KafkaTemplate<String, UserAccountCreationEvent> kafkaTemplate, NewTopic userAccountCreationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.userAccountCreationTopic = userAccountCreationTopic;
    }

    public void sendUserAccountCreationMessage(String key, UserAccountCreationEvent event) {
        // Send event
        kafkaTemplate.send(userAccountCreationTopic.name(), key, event);

        LOGGER.info(String.format("User account creation event sent -> %s", event.toString()));
    }
}
