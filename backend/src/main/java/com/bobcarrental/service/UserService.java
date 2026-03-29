package com.bobcarrental.service;

import com.bobcarrental.dto.request.UserRequest;
import com.bobcarrental.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for User management
 */
public interface UserService {

    /**
     * Get all users with pagination
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Get all users without pagination
     */
    List<UserResponse> getAllUsers();

    /**
     * Get user by ID
     */
    UserResponse getUserById(Long id);

    /**
     * Create new user
     */
    UserResponse createUser(UserRequest request);

    /**
     * Update existing user
     */
    UserResponse updateUser(Long id, UserRequest request);

    /**
     * Delete user
     */
    void deleteUser(Long id);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
}

// Made with Bob