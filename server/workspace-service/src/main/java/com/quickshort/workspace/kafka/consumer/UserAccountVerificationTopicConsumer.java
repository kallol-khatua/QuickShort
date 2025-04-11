package com.quickshort.workspace.kafka.consumer;

import com.quickshort.common.enums.UserAccountStatus;
import com.quickshort.common.events.UserAccountVerificationEvent;
import com.quickshort.workspace.models.User;
import com.quickshort.workspace.repository.UserRepository;
import com.quickshort.workspace.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountVerificationTopicConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountVerificationTopicConsumer.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "user-account-verified")
    public void userAccountVerification(UserAccountVerificationEvent event) {
        try {
            LOGGER.info("user account verification event received in workspace service -> {}", event);

            Optional<User> existingUser = userRepository.findById(event.getUserPayload().getId());
            if(existingUser.isEmpty()) {
                throw new Exception("Account not found with id -> " + event.getUserPayload().getId());
            }

            User user = existingUser.get();
            user.setStatus(UserAccountStatus.ACTIVE);

            User savedUser = userRepository.save(user);

            LOGGER.info("user account updated -> {}", savedUser.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing message: {}", e.getMessage(), e);
            // Optionally: Send failed events to a dead-letter topic (DLT)
        }
    }
}
