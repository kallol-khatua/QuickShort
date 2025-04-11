package com.quickshort.auth.service;


import com.quickshort.auth.dto.UserDto;

import java.util.UUID;

public interface UserService {
    UserDto registerUser(UserDto userDto);
    UserDto verifyAccount(UUID userId);
    String verifyUser(UserDto userDto);
}