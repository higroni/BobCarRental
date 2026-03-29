package com.bobcarrental.service;

import com.bobcarrental.dto.request.LoginRequest;
import com.bobcarrental.dto.response.AuthResponse;

/**
 * Authentication service interface.
 * Handles user authentication and token generation.
 */
public interface AuthService {
    
    /**
     * Authenticate user and generate JWT tokens
     */
    AuthResponse login(LoginRequest request);
    
    /**
     * Refresh access token using refresh token
     */
    AuthResponse refreshToken(String refreshToken);
    
    /**
     * Logout user (invalidate tokens)
     */
    void logout(String token);
}

// Made with Bob
