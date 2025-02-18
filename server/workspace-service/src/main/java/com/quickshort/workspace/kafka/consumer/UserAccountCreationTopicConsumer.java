package com.quickshort.workspace.kafka.consumer;

import com.quickshort.common.events.UserAccountCreationEvent;
import com.quickshort.workspace.models.User;
import com.quickshort.workspace.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserAccountCreationTopicConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountCreationTopicConsumer.class);

    @Autowired
    private UserService userService;

    @KafkaListener(topics = "user-account-creation")
    public void userAccountCreation(UserAccountCreationEvent event) {
        try {
            LOGGER.info("user account creation event received in workspace service -> {}", event);

            User saveduser = userService.createUser(event.getUserPayload());

            LOGGER.info("user account saved -> {}", saveduser);
        } catch (Exception e) {
            LOGGER.error("Error processing message: {}", e.getMessage(), e);
            // Optionally: Send failed events to a dead-letter topic (DLT)
        }
    }
}
