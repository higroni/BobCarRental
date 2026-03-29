package com.bobcarrental.service.impl;

import com.bobcarrental.dto.request.LoginRequest;
import com.bobcarrental.dto.response.AuthResponse;
import com.bobcarrental.model.Role;
import com.bobcarrental.model.User;
import com.bobcarrental.repository.UserRepository;
import com.bobcarrental.security.JwtTokenProvider;
import com.bobcarrental.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Authentication service implementation.
 * Handles user login, token generation, and token refresh.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.debug("Attempting login for user: {}", request.getUsername());
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate tokens
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        
        // Get user details
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + request.getUsername()));
        
        // Build user info
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .enabled(user.isActive())
                .build();
        
        // Build response
        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000) // Convert to seconds
                .user(userInfo)
                .timestamp(LocalDateTime.now())
                .build();
        
        log.info("User logged in successfully: {}", request.getUsername());
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        log.debug("Refreshing access token");
        
        // Validate refresh token
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        // Get username from token
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        
        // Load user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));
        
        if (!user.isActive()) {
            throw new RuntimeException("User is not active");
        }
        
        // Create authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                user.getRoles().stream()
                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toList())
        );
        
        // Generate new access token
        String newAccessToken = tokenProvider.generateToken(authentication);
        
        // Build user info
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .enabled(user.isActive())
                .build();
        
        // Build response
        AuthResponse response = AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Keep same refresh token
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .user(userInfo)
                .timestamp(LocalDateTime.now())
                .build();
        
        log.info("Access token refreshed for user: {}", username);
        return response;
    }
    
    @Override
    public void logout(String token) {
        log.debug("Logging out user");
        
        // Clear security context
        SecurityContextHolder.clearContext();
        
        // Note: In a production system, you would:
        // 1. Add token to blacklist/revocation list
        // 2. Store in Redis with TTL = token expiration
        // 3. Check blacklist in JwtAuthenticationFilter
        
        log.info("User logged out successfully");
    }
}

// Made with Bob
