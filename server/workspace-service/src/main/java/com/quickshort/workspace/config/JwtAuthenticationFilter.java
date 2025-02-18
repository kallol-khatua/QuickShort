package com.quickshort.workspace.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickshort.common.exception.CommonException;
import com.quickshort.common.exception.FieldError;
import com.quickshort.common.exception.UnauthorizedException;
import com.quickshort.workspace.service.impl.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            handleException(response, new UnauthorizedException(
                    "Missing or invalid token",
                    "Provide a valid token",
                    List.of(new FieldError("Authorization header is missing or invalid", "auth_token"))
            ));
            return;
        }

        String jwt = authHeader.substring(7); // Remove "Bearer " prefix
        if (jwt.isEmpty()) {
            handleException(response, new UnauthorizedException(
                    "Missing or invalid token",
                    "Provide a valid token",
                    List.of(new FieldError("Invalid token format", "auth_token"))
            ));
            return;
        }

        try {
            String username = jwtService.extractUsername(jwt);

            if (username.isEmpty()) {
                throw new Exception("Invalid or expired token");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                throw new Exception("Invalid or expired token");
            }

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new Exception("Invalid or expired token");
            }
        } catch (Exception e) {
            handleException(response, new UnauthorizedException(
                    "Invalid or expired token",
                    "Sign in to your account",
                    List.of(new FieldError("Invalid or expired token", "auth_token"))
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletResponse response, CommonException ex) throws IOException {
        response.setStatus(ex.getStatus_code());
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();  // Create ObjectMapper instance

        Map<String, Object> errorResponse = ex.serializeErrors();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse)); // Convert to JSON and write to response
    }
}
