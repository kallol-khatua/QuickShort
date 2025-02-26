package com.quickshort.payment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.workspaceType-upgradation-topic}")
    private String workspaceTypeUpgradationTopicName;

    @Bean
    public NewTopic workspaceTypeUpgradationTopic() {
        return TopicBuilder.name(workspaceTypeUpgradationTopicName).build();
    }
}
