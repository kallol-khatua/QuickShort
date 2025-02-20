package com.quickshort.workspace.service;

import com.quickshort.workspace.dto.WorkspaceMemberDto;

import java.util.List;
import java.util.UUID;

public interface WorkspaceMemberService {
    List<WorkspaceMemberDto> allWorkspaceWhereUserIsMemberOrOwner();

    List<WorkspaceMemberDto> allWorkspaceWhereUserIsOwner();

    List<WorkspaceMemberDto> allWorkspaceWhereUserIsMember();

    WorkspaceMemberDto joinAsMember(UUID workspaceId);

    WorkspaceMemberDto joinAsOwner(UUID workspaceId);

    List<WorkspaceMemberDto> getAllMembers(UUID workspaceId);

    WorkspaceMemberDto verifyMember(UUID workspaceId, UUID workspaceMemberId, boolean isVerified);
}
