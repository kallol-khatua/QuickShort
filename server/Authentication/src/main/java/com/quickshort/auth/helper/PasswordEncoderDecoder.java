package com.quickshort.auth.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderDecoder {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String hashPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
