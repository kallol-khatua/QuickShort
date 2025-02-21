package com.quickshort.workspace.service.impl;

import com.quickshort.common.exception.FieldError;
import com.quickshort.common.exception.InternalServerErrorException;
import com.quickshort.common.payload.UserPayload;

import com.quickshort.workspace.models.User;

import com.quickshort.workspace.repository.UserRepository;
import com.quickshort.workspace.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(UserPayload payload) {
        try {
            User newUser = getNewUser(payload);

            return userRepository.save(newUser);
        } catch (Exception e) {
            LOGGER.error("Unexpected error during user account creation", e);
            return null;
        }
    }

    private User getNewUser(UserPayload payload) {
        User newUser = new User();

        newUser.setId(payload.getId());
        newUser.setEmail(payload.getEmail());
        newUser.setPassword(payload.getPassword());
        newUser.setRole(payload.getRole());
        newUser.setStatus(payload.getStatus());
        newUser.setAccountNonExpired(payload.isAccountNonExpired());
        newUser.setCredentialsNonExpired(payload.isCredentialsNonExpired());
        newUser.setAccountNonLocked(payload.isAccountNonLocked());
        newUser.setEnabled(payload.isEnabled());

        return newUser;
    }
}
