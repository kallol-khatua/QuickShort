package com.quickshort.workspace.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quickshort.common.enums.MemberStatus;
import com.quickshort.common.enums.MemberType;
import com.quickshort.common.enums.ShortUrlStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.apachecommons.CommonsLog;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "short_urls")
public class ShortUrl implements Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private Workspace workspaceId;

    @ManyToOne
    @JoinColumn(name = "workspace_member_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private WorkspaceMember workspaceMemberID;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "short_code", nullable = false, unique = true, length = 10)
    private String shortCode;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShortUrlStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
