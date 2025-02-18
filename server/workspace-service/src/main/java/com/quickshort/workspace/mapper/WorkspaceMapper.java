package com.quickshort.workspace.mapper;

import com.quickshort.workspace.dto.WorkspaceDto;
import com.quickshort.workspace.models.Workspace;

public class WorkspaceMapper {
    public static Workspace mapToWorkspace(WorkspaceDto workspaceDto) {
        Workspace newWorkspace = new Workspace();

        newWorkspace.setName(workspaceDto.getName());
        newWorkspace.setType(workspaceDto.getType());

        return newWorkspace;
    }

    public static WorkspaceDto mapToWorkspaceDto(Workspace workspace) {
        WorkspaceDto newDto = new WorkspaceDto();

        newDto.setId(workspace.getId());
        newDto.setName(workspace.getName());
        newDto.setCreatedBy(workspace.getCreatedBy());
        newDto.setType(workspace.getType());
        newDto.setLinkCreationLimitPerMonth(workspace.getLinkCreationLimitPerMonth());
        newDto.setCreatedLinksThisMonth(workspace.getCreatedLinksThisMonth());

        return newDto;
    }
}
