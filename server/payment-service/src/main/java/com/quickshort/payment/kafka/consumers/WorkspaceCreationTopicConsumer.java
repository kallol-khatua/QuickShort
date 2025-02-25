package com.quickshort.payment.kafka.consumers;

import com.quickshort.common.events.WorkspaceCreationEvent;
import com.quickshort.payment.models.Workspace;
import com.quickshort.payment.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceCreationTopicConsumer {
    @Autowired
    private WorkspaceService workspaceService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceCreationTopicConsumer.class);

    @KafkaListener(topics = "workspace-creation")
    public void workspaceCreation(WorkspaceCreationEvent event) {
        try {
            LOGGER.info("workspace creation event received in payment service -> {}", event);

            Workspace savedWorkspace = workspaceService.saveWorkspace(event.getWorkspacePayload());

            LOGGER.info("workspace saved -> {}", savedWorkspace);
        } catch (Exception e) {
            LOGGER.error("Error processing message: {}", e.getMessage(), e);
        }
    }
}
