package com.quickshort.auth.controllers;

import com.quickshort.auth.dto.UserDto;
import com.quickshort.auth.service.UserService;
//import jakarta.servlet.http.HttpServletRequest;
import com.quickshort.common.dto.SuccessApiResponse;
import com.quickshort.common.exception.BadRequestException;
import com.quickshort.common.exception.FieldError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = {"/register", "/register/"})
    public ResponseEntity<SuccessApiResponse<UserDto>> registerUser(@RequestBody(required = false) UserDto user) {
        if (user == null) {
            user = new UserDto();
        }

        // Register user
        UserDto registeredUser = userService.registerUser(user);

        // Set up response
        SuccessApiResponse<UserDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.CREATED.value());
        response.setStatus_text(HttpStatus.CREATED.name());
        response.setSuccess(true);
        response.setStatus("Account Created");
        response.setMessage("New user account created");
        response.setData(registeredUser);

        // Return the response with 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = {"/verify-account", "/verify-account/"})
    public ResponseEntity<SuccessApiResponse<UserDto>> verifyAccount(@RequestParam(required = false) String id) {
        // Checking id is provided or not
        if (id == null || id.isBlank()) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Missing required query parameter: 'id'", "id"));
            throw new BadRequestException("All filed not provided", "Missing required query parameter: 'id'", errors);
        }

        // check id is a valid uuid or not
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Invalid id", "id"));
            throw new BadRequestException("Invalid id", "Invalid id", errors);
        }

        UserDto userDto = userService.verifyAccount(userId);

        // Set up response
        SuccessApiResponse<UserDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Account verified");
        response.setMessage("Account verified successfully");
        response.setData(userDto);

        // Return the response with 200 Created status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = {"/signin", "/signin/"})
    public ResponseEntity<SuccessApiResponse<HashMap<String, String>>> loginUser(@RequestBody(required = false) UserDto user) {
        if (user == null) {
            user = new UserDto();
        }

        // Generate token
        String token = userService.verifyUser(user);

        // Set up response
        SuccessApiResponse<HashMap<String, String>> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Logged IN");
        response.setMessage("Logged IN successfully");
        HashMap<String, String> data = new HashMap<>();
        data.put("token", token);
        response.setData(data);

        // Return the response with 200 Created status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
