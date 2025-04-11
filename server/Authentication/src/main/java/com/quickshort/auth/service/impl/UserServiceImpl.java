package com.quickshort.auth.service.impl;

import com.quickshort.auth.dto.UserDto;
import com.quickshort.auth.helper.JwtUtil;
import com.quickshort.auth.helper.PasswordEncoderDecoder;
import com.quickshort.auth.kafka.producer.UserAccountCreationProducer;
import com.quickshort.auth.kafka.producer.UserAccountVerificationProducer;
import com.quickshort.auth.mappers.UserMapper;
import com.quickshort.auth.models.User;
import com.quickshort.auth.repository.UserRepository;
import com.quickshort.auth.service.UserService;
import com.quickshort.common.enums.UserAccountStatus;
import com.quickshort.common.enums.UserRole;
import com.quickshort.common.events.UserAccountCreationEvent;
import com.quickshort.common.events.UserAccountVerificationEvent;
import com.quickshort.common.exception.BadRequestException;
import com.quickshort.common.exception.FieldError;
import com.quickshort.common.exception.InternalServerErrorException;
import com.quickshort.common.payload.UserPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAccountCreationProducer userAccountCreationProducer;

    @Autowired
    private UserAccountVerificationProducer userAccountVerificationProducer;

    @Autowired
    private PasswordEncoderDecoder passwordEncoderDecoder;

    @Autowired
    private JwtUtil jwtUtil;

    // TODO: Add data validation email format, password length condition
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDto registerUser(UserDto userDto) {
        try {
            List<FieldError> errors = new ArrayList<>();
            if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
                errors.add(new FieldError("Email is required", "email"));
            }
            if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
                errors.add(new FieldError("Password is required", "password"));
            }
            if (userDto.getFirstName() == null || userDto.getFirstName().isEmpty()) {
                errors.add(new FieldError("First name is required", "firstName"));
            }
            if (userDto.getLastName() == null || userDto.getLastName().isEmpty()) {
                errors.add(new FieldError("Last name is required", "lastName"));
            }
            if (!errors.isEmpty()) {
                throw new BadRequestException("Provide all details", "Please provide all details", errors);
            }

            User newUser = UserMapper.mapToUser(userDto);

//            if email already registered
            User existingUser = userRepository.findByEmail(newUser.getEmail());
            if (existingUser != null) {
                errors.add(new FieldError("Email already taken", "email"));
                throw new BadRequestException("Email already taken", "Email already taken", errors);
            }

            String hashedPassword = passwordEncoderDecoder.hashPassword(userDto.getPassword());
            newUser.setPassword(hashedPassword);
            newUser.setStatus(UserAccountStatus.PENDING_VERIFICATION);
            newUser.setRole(UserRole.USER);
            newUser.setAccountNonExpired(true);
            newUser.setAccountNonLocked(true);
            newUser.setCredentialsNonExpired(true);
            newUser.setEnabled(true);

            // Save user to DB
            User savedUser = userRepository.save(newUser);

            // Send user account creation event to kafka
            UserAccountCreationEvent event = new UserAccountCreationEvent();
            event.setStatus("Account Created");
            event.setMessage("New user account created");
            event.setKey(String.valueOf(savedUser.getId()));
            // Set payload
            UserPayload payload = getPayload(savedUser);
            event.setUserPayload(payload);
            userAccountCreationProducer.sendUserAccountCreationMessage(event.getKey(), event);

            // Return created user
            return UserMapper.mapToUserDto(savedUser);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during user account creation", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }

    private UserPayload getPayload(User savedUser) {
        UserPayload payload = new UserPayload();

        payload.setId(savedUser.getId());
        payload.setFirstName(savedUser.getFirstName());
        payload.setLastName(savedUser.getLastName());
        payload.setEmail(savedUser.getEmail());
        payload.setPassword(savedUser.getPassword());
        payload.setRole(savedUser.getRole());
        payload.setStatus(savedUser.getStatus());
        payload.setAccountNonExpired(savedUser.isAccountNonExpired());
        payload.setAccountNonLocked(savedUser.isAccountNonLocked());
        payload.setCredentialsNonExpired(savedUser.isCredentialsNonExpired());
        payload.setEnabled(savedUser.isEnabled());
        payload.setCreatedAt(savedUser.getCreatedAt());
        payload.setUpdatedAt(savedUser.getUpdatedAt());

        return payload;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDto verifyAccount(UUID userId) {
        try {
            List<FieldError> errors = new ArrayList<>();

//            check user exist with the id or not if not exist then throw error
            Optional<User> existingUser = userRepository.findById(userId);
            if (existingUser.isEmpty()) {
                errors.add(new FieldError("Invalid id", "id"));
                throw new BadRequestException("Invalid id", "Invalid id", errors);
            }

            User user = existingUser.get();

            if (user.getStatus() != UserAccountStatus.PENDING_VERIFICATION) {
                errors.add(new FieldError("Account already verified"));
                throw new BadRequestException("Account already verified", "Account already verified", errors);
            }

            user.setStatus(UserAccountStatus.ACTIVE);

            // Save user to DB
            User savedUser = userRepository.save(user);
            LOGGER.info("Account verified for id -> {}", userId);

            // Send user verification event to kafka
            UserAccountVerificationEvent event = new UserAccountVerificationEvent();
            event.setStatus("Account verified");
            event.setMessage("user account verified successfully");
            event.setKey(String.valueOf(savedUser.getId()));
            // Set payload
            UserPayload payload = getPayload(savedUser);
            event.setUserPayload(payload);
            userAccountVerificationProducer.sendUserAccountVerificationMessage(event.getKey(), event);

            // Return created user
            return UserMapper.mapToUserDto(savedUser);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during user account creation", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }

    // TODO: Add data validation email format, password length condition
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public String verifyUser(UserDto userDto) {
        try {

            List<FieldError> errors = new ArrayList<>();
            if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
                errors.add(new FieldError("Email is required", "email"));
            }
            if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
                errors.add(new FieldError("Password is required", "password"));
            }
            if (!errors.isEmpty()) {
                throw new BadRequestException("Provide all details", "Please provide all details", errors);
            }

            User user = userRepository.findByEmail(userDto.getEmail());

            // If user not found with the email then throw bad request exception
            if (user == null) {
                errors = new ArrayList<>();
                errors.add(new FieldError("Invalid Email or Password"));
                throw new BadRequestException("Invalid Credentials", "Invalid Email or Password", errors);
            }

            // If wrong password is provided then throw bad request exception
            if (!passwordEncoderDecoder.verifyPassword(userDto.getPassword(), user.getPassword())) {
                errors = new ArrayList<>();
                errors.add(new FieldError("Invalid Email or Password"));
                throw new BadRequestException("Invalid Credentials", "Invalid Email or Password", errors);
            }

            if (user.getStatus() != UserAccountStatus.ACTIVE) {
                errors = new ArrayList<>();
                errors.add(new FieldError("Account not active"));
                throw new BadRequestException("Account not active", "Account not active", errors);
            }

            if (!user.isAccountNonExpired() || !user.isAccountNonLocked() || !user.isCredentialsNonExpired() || !user.isEnabled()) {
                errors = new ArrayList<>();
                errors.add(new FieldError("Account not active"));
                throw new BadRequestException("Account not active", "Account not active", errors);
            }

            // Return token for after user is verified
            return jwtUtil.generateToken(user);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during user verification", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }
}
