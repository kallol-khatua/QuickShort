package com.quickshort.auth.mappers;

import com.quickshort.auth.dto.UserDto;
import com.quickshort.auth.models.User;

public class UserMapper {
    public static User mapToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setLastName(userDto.getLastName());
        user.setFirstName(userDto.getFirstName());

        return user;
    }

    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword()
        );
    }
}
