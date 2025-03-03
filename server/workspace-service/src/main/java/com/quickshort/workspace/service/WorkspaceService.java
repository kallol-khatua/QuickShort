package com.quickshort.workspace.service;

import com.quickshort.common.payload.WorkspacePayload;
import com.quickshort.workspace.dto.WorkspaceDto;
import com.quickshort.workspace.models.Workspace;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface WorkspaceService {
    WorkspaceDto createWorkspace(WorkspaceDto workspaceDto);

    Workspace workspaceTypeUpgrade(WorkspacePayload payload);
}
