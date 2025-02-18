package com.quickshort.workspace.dto;

import com.quickshort.common.enums.MemberStatus;
import com.quickshort.common.enums.MemberType;
import com.quickshort.workspace.models.User;
import com.quickshort.workspace.models.Workspace;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkspaceMemberDto {
    private UUID id;
    private Workspace workspaceId;
    private User userId;
    private MemberType memberType;
    private MemberStatus status;
}
