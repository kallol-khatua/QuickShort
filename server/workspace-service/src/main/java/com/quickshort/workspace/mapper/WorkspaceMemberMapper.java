package com.quickshort.workspace.mapper;

import com.quickshort.workspace.dto.WorkspaceMemberDto;
import com.quickshort.workspace.models.WorkspaceMember;

public class WorkspaceMemberMapper {

    public static WorkspaceMemberDto mapToWorkspaceMemberDto(WorkspaceMember workspaceMember) {
        WorkspaceMemberDto workspaceMemberDto = new WorkspaceMemberDto();

        workspaceMemberDto.setId(workspaceMember.getId());
        workspaceMemberDto.setWorkspaceId(workspaceMember.getWorkspaceId());
        workspaceMemberDto.setUserId(workspaceMember.getUserId());
        workspaceMemberDto.setMemberType(workspaceMember.getMemberType());
        workspaceMemberDto.setStatus(workspaceMember.getStatus());

        return workspaceMemberDto;
    }
}
