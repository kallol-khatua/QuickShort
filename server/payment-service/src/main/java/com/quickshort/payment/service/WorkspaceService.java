package com.quickshort.payment.service;

import com.quickshort.common.payload.WorkspacePayload;
import com.quickshort.payment.models.Workspace;

import java.util.Optional;

public interface WorkspaceService {
    Workspace saveWorkspace(WorkspacePayload payload);
}
