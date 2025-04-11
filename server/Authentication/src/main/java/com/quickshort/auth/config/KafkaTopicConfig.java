package com.quickshort.auth.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userAccountCreationTopic() {
        return TopicBuilder.name("user-account-creation").build();
    }
    @Bean
    public NewTopic userAccountVerificationTopic() {
        return TopicBuilder.name("user-account-verified").build();
    }
}