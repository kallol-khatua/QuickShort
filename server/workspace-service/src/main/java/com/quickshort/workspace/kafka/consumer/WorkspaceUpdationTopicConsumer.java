package com.quickshort.workspace.kafka.consumer;

import com.quickshort.common.events.WorkspaceTypeUpgradationEvent;
import com.quickshort.workspace.models.Workspace;
import com.quickshort.workspace.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceUpdationTopicConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceUpdationTopicConsumer.class);

    @Autowired
    private WorkspaceService workspaceService;

    @KafkaListener(topics = "${spring.kafka.workspace-updation-topic}")
    public void workspaceUpdation(WorkspaceTypeUpgradationEvent event) {
        try {
            LOGGER.info("workspace updation event received in workspace service -> {}", event);

            Workspace upgradedWorkspace = workspaceService.workspaceUpdate(event.getWorkspacePayload());

            LOGGER.info("workspace details updated -> {}", upgradedWorkspace.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing workspace updation message: {}", e.getMessage(), e);
        }
    }
}
