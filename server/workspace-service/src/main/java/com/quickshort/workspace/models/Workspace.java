package com.quickshort.workspace.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.quickshort.common.enums.WorkspaceType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "workspaces")
public class Workspace implements Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @JsonBackReference
    private User createdBy;

    @JsonManagedReference
    @OneToMany(mappedBy = "workspaceId")
    @JsonIgnore
    private List<WorkspaceMember> workspaceMembers;

    @JsonManagedReference
    @OneToMany(mappedBy = "workspaceId")
    @JsonIgnore
    private List<ShortUrl> shortUrls;

    @Enumerated(EnumType.STRING)
    @Column(name = "workspace_type", nullable = false)
    private WorkspaceType type;

    @Column(name = "link_creation_limit_per_month", nullable = false)
    private int linkCreationLimitPerMonth;

    @Column(name = "created_links_this_month", nullable = false)
    private int createdLinksThisMonth;

    @Column(name = "member_limit", nullable = false)
    private int memberLimit;

    @Column(name = "member_count", nullable = false)
    private int memberCount;

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
