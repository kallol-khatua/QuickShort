package com.quickshort.workspace.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.quickshort.common.enums.UserAccountStatus;
import com.quickshort.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email_id", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserAccountStatus status;

    @Column(name = "is_account_non_expired", nullable = false)
    private boolean isAccountNonExpired;

    @Column(name = "is_credentials_non_expired", nullable = false)
    private boolean isCredentialsNonExpired;

    @Column(name = "is_account_non_locked", nullable = false)
    private boolean isAccountNonLocked;

    // set default to false, after verifying email set this to true
    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;

    @JsonManagedReference
    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private List<Workspace> workspaces;

    @JsonManagedReference
    @OneToMany(mappedBy = "userId")
    @JsonIgnore
    private List<WorkspaceMember> workspaceMembers;

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
