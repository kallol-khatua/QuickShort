package com.quickshort.auth.service;


import com.quickshort.auth.dto.UserDto;

public interface UserService {
    UserDto registerUser(UserDto userDto);

    String verifyUser(UserDto userDto);
}