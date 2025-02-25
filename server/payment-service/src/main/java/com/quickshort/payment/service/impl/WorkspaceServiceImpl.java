package com.quickshort.payment.service.impl;

import com.quickshort.common.payload.WorkspacePayload;
import com.quickshort.payment.models.Workspace;
import com.quickshort.payment.repository.WorkspaceRepository;
import com.quickshort.payment.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceServiceImpl.class);

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Override
    public Workspace saveWorkspace(WorkspacePayload payload) {
        try {
            Workspace newWorkspace = getWorkspace(payload);

            return workspaceRepository.save(newWorkspace);
        } catch (Exception e) {
            LOGGER.error("Unexpected error during workspace creation", e);
            return null;
        }
    }

    // Function to get workspace from workspace payload
    private Workspace getWorkspace(WorkspacePayload payload) {
        Workspace workspace = new Workspace();

        workspace.setId(payload.getId());
        workspace.setType(payload.getType());
        workspace.setCreatedAt(payload.getCreatedAt());
        workspace.setUpdatedAt(payload.getUpdatedAt());

        workspace.setLinkCreationLimitPerMonth(payload.getLinkCreationLimitPerMonth());
        workspace.setMemberLimit(payload.getMemberLimit());

        workspace.setLastResetDate(payload.getLastResetDate());
        workspace.setNextResetDate(payload.getNextResetDate());
        workspace.setNextBillingDate(payload.getNextBillingDate());

        workspace.setWorkspaceStatus(payload.getWorkspaceStatus());

        return workspace;
    }
}
