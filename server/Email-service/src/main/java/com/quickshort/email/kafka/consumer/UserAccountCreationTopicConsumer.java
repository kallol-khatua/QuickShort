package com.quickshort.email.kafka.consumer;

import com.quickshort.common.events.UserAccountCreationEvent;
import com.quickshort.email.service.UserAccountCreationMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserAccountCreationTopicConsumer {
    @Autowired
    private UserAccountCreationMail userAccountCreationMail;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountCreationTopicConsumer.class);

    @KafkaListener(topics = "user-account-creation")
    public void userAccountCreation(UserAccountCreationEvent event) {
        try {
            LOGGER.info("Message received in email service -> {}", event);

            UUID id = event.getUserPayload().getId();
            String email = event.getUserPayload().getEmail();

            userAccountCreationMail.sendEmail(
                    email,
                    "Verify your email",
                    id
            );
        } catch (Exception e) {
            LOGGER.error("Error processing message: {}", e.getMessage(), e);
            // Optionally: Send failed events to a dead-letter topic (DLT)
        }
    }
}
