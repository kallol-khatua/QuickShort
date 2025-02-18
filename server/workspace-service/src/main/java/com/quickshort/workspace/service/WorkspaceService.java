package com.quickshort.workspace.service;

import com.quickshort.workspace.dto.WorkspaceDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface WorkspaceService {
    WorkspaceDto createWorkspace(WorkspaceDto workspaceDto);

    WorkspaceDto updateWorkspace(UUID workspaceId, WorkspaceDto workspaceDto);
}
