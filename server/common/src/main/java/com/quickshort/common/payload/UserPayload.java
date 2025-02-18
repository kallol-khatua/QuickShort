package com.quickshort.common.payload;

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
public class UserPayload {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
    private UserAccountStatus status;
    private boolean isAccountNonExpired;
    private boolean isCredentialsNonExpired;
    private boolean isAccountNonLocked;
    private boolean isEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
