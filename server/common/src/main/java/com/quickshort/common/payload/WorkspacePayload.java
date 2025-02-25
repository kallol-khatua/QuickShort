package com.quickshort.common.payload;

import com.quickshort.common.enums.WorkspaceStatus;
import com.quickshort.common.enums.WorkspaceType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkspacePayload {
    private UUID id;
    private WorkspaceType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int linkCreationLimitPerMonth;
    private int memberLimit;

    private LocalDate lastResetDate;
    private LocalDate nextResetDate;
    private LocalDate nextBillingDate;

    private WorkspaceStatus workspaceStatus;
}
