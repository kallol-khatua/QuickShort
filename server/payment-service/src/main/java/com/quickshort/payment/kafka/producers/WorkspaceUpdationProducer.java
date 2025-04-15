package com.quickshort.payment.kafka.producers;

import com.quickshort.common.events.WorkspaceTypeUpgradationEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceUpdationProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceUpdationProducer.class);

    private final KafkaTemplate<String, WorkspaceTypeUpgradationEvent> kafkaTemplate;
    private final NewTopic workspaceUpdatedTopic;

    public WorkspaceUpdationProducer(KafkaTemplate<String, WorkspaceTypeUpgradationEvent> kafkaTemplate, NewTopic workspaceUpdatedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.workspaceUpdatedTopic = workspaceUpdatedTopic;
    }

    public void workspaceUpdationMessage(String key, WorkspaceTypeUpgradationEvent event) {
        // Send event
        kafkaTemplate.send(workspaceUpdatedTopic.name(), key, event);

        LOGGER.info(String.format("Workspace updation event sent -> %s", event.toString()));
    }
}
