package com.quickshort.auth.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
