package com.quickshort.auth.kafka.producer;

import com.quickshort.common.events.UserAccountVerificationEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserAccountVerificationProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountVerificationProducer.class);

    private final KafkaTemplate<String, UserAccountVerificationEvent> kafkaTemplate;
    private final NewTopic userAccountVerificationTopic;

    public UserAccountVerificationProducer(KafkaTemplate<String, UserAccountVerificationEvent> kafkaTemplate, NewTopic userAccountVerificationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.userAccountVerificationTopic = userAccountVerificationTopic;
    }

    public void sendUserAccountVerificationMessage(String key, UserAccountVerificationEvent event) {
        // Send event
        kafkaTemplate.send(userAccountVerificationTopic.name(), key, event);

        LOGGER.info(String.format("User account verification event sent -> %s", event.toString()));
    }
}
