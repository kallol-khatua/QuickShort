package com.quickshort.workspace.dto;

import com.quickshort.common.enums.WorkspaceType;
import com.quickshort.workspace.models.User;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkspaceDto implements Serializable {
    private UUID id;
    private String name;
    private User createdBy;
    private WorkspaceType type;
    private int linkCreationLimitPerMonth;
    private int createdLinksThisMonth;
    private int memberLimit;
    private int memberCount;
}
