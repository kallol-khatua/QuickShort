package com.quickshort.analytics.dto;

import com.quickshort.common.enums.ShortUrlStatus;
import lombok.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShortUrlDto {

    private UUID id;
    private UUID workspaceId;
    private UUID workspaceMemberID;
    private String originalUrl;
    private String shortCode;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private ShortUrlStatus status;
}