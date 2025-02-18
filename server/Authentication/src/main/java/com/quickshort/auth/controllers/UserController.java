package com.quickshort.auth.controllers;

import com.quickshort.auth.dto.UserDto;
import com.quickshort.auth.service.UserService;
//import jakarta.servlet.http.HttpServletRequest;
import com.quickshort.common.dto.SuccessApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<SuccessApiResponse<UserDto>> registerUser(@RequestBody UserDto user) {
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

    @PostMapping("/login")
    public ResponseEntity<SuccessApiResponse<HashMap<String, String>>> loginUser(@RequestBody UserDto user) {
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
