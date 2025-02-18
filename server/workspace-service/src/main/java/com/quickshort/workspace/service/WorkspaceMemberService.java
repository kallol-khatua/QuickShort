package com.quickshort.workspace.service;

import com.quickshort.workspace.dto.WorkspaceMemberDto;

import java.util.List;

public interface WorkspaceMemberService {
    List<WorkspaceMemberDto> allWorkspaceWhereUserIsMemberOrOwner();
    List<WorkspaceMemberDto> allWorkspaceWhereUserIsOwner();
    List<WorkspaceMemberDto> allWorkspaceWhereUserIsMember();
}
