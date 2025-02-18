package com.quickshort.auth.helper;

import com.quickshort.auth.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtil {
    @Value("${spring.jwt-secret}")
    private String secretKey;

    @Value("${spring.jwt-expiration}")
    private long EXPIRATION_TIME;

    // Generate JWT Token
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("id", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("status", user.getStatus().name());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
