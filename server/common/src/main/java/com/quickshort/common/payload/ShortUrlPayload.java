package com.quickshort.common.payload;

import com.quickshort.common.enums.ShortUrlStatus;
import com.quickshort.common.enums.UserAccountStatus;
import com.quickshort.common.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ShortUrlPayload {
    private UUID id;
    private UUID workspaceId;
    private UUID workspaceMemberID;
    private String originalUrl;
    private String shortCode;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private ShortUrlStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
