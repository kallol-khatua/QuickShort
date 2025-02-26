package com.quickshort.payment.kafka.producers;

import com.quickshort.common.events.WorkspaceTypeUpgradationEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceTypeUpgradationProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceTypeUpgradationProducer.class);

    private final KafkaTemplate<String, WorkspaceTypeUpgradationEvent> kafkaTemplate;
    private final NewTopic workspaceTypeUpgradationTopic;

    public WorkspaceTypeUpgradationProducer(KafkaTemplate<String, WorkspaceTypeUpgradationEvent> kafkaTemplate, NewTopic workspaceTypeUpgradationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.workspaceTypeUpgradationTopic = workspaceTypeUpgradationTopic;
    }

    public void workspaceTypeUpgradationMessage(String key, WorkspaceTypeUpgradationEvent event) {
        // Send event
        kafkaTemplate.send(workspaceTypeUpgradationTopic.name(), key, event);

        LOGGER.info(String.format("Workspace Type Upgradation event sent -> %s", event.toString()));
    }
}
