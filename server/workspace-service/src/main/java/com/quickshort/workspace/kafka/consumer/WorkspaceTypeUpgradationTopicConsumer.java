package com.quickshort.workspace.kafka.consumer;

import com.quickshort.common.events.WorkspaceTypeUpgradationEvent;
import com.quickshort.workspace.models.User;
import com.quickshort.workspace.models.Workspace;
import com.quickshort.workspace.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceTypeUpgradationTopicConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceTypeUpgradationTopicConsumer.class);

    @Autowired
    private WorkspaceService workspaceService;

    @KafkaListener(topics = "workspace-upgradation")
    public void workspaceTypeUpgradation(WorkspaceTypeUpgradationEvent event) {
        try {
            LOGGER.info("workspace type upgradation event received in workspace service -> {}", event);

            Workspace upgradedWorkspace = workspaceService.workspaceTypeUpgrade(event.getWorkspacePayload());

            LOGGER.info("workspace details updated -> {}", upgradedWorkspace.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing workspaceTypeUpgradation message: {}", e.getMessage(), e);
        }
    }
}
