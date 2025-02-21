package com.quickshort.workspace.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.url-creation-topic}")
    private String urlCreationTopicName;

    @Bean
    public NewTopic urlCreationTopic() {
        return TopicBuilder.name(urlCreationTopicName).build();
    }
}
