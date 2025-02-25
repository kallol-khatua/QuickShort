package com.quickshort.workspace.kafka.producers;

import com.quickshort.common.events.WorkspaceCreationEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceCreationProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceCreationProducer.class);

    private final KafkaTemplate<String, WorkspaceCreationEvent> kafkaTemplate;
    private final NewTopic workspaceCreationTopic;

    public WorkspaceCreationProducer(KafkaTemplate<String, WorkspaceCreationEvent> kafkaTemplate, NewTopic workspaceCreationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.workspaceCreationTopic = workspaceCreationTopic;
    }

    public void sendWorkspaceCreationMessage(String key, WorkspaceCreationEvent event) {
        // Send event
        kafkaTemplate.send(workspaceCreationTopic.name(), key, event);

        LOGGER.info(String.format("Workspace Creation event sent -> %s", event.toString()));
    }
}
