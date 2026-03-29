package com.bobcarrental.service.impl;

import com.bobcarrental.dto.request.UserRequest;
import com.bobcarrental.dto.response.UserResponse;
import com.bobcarrental.exception.DuplicateResourceException;
import com.bobcarrental.exception.ResourceNotFoundException;
import com.bobcarrental.mapper.UserMapper;
import com.bobcarrental.model.Role;
import com.bobcarrental.model.User;
import com.bobcarrental.repository.RoleRepository;
import com.bobcarrental.repository.UserRepository;
import com.bobcarrental.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for User management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Getting all users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Getting all users without pagination");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Getting user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        log.debug("Creating new user: {}", request.getUsername());

        // Check for duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Validate password is provided for new user
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required for new user");
        }

        // Create user entity
        User user = userMapper.toEntity(request);
        
        // Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set roles
        Set<Role> roles = getRolesFromRequest(request);
        user.setRoles(roles);

        // Set default values if not provided
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }

        User savedUser = userRepository.save(user);
        log.info("Created user: {}", savedUser.getUsername());
        
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        log.debug("Updating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Check for duplicate username (if changed)
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        // Check for duplicate email (if changed)
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Update basic fields
        userMapper.updateEntity(request, user);

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update roles if provided
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = getRolesFromRequest(request);
            user.getRoles().clear();
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        log.info("Updated user: {}", updatedUser.getUsername());
        
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Deleting user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Prevent deleting the last admin
        if (user.isAdmin()) {
            long adminCount = userRepository.findAll().stream()
                    .filter(User::isAdmin)
                    .count();
            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot delete the last admin user");
            }
        }

        userRepository.delete(user);
        log.info("Deleted user: {}", user.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Helper method to get roles from request
     */
    private Set<Role> getRolesFromRequest(UserRequest request) {
        Set<Role> roles = new HashSet<>();

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            // Default to USER role if no roles specified
            Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_USER"));
            roles.add(userRole);
        } else {
            for (String roleName : request.getRoles()) {
                try {
                    Role.RoleName roleEnum = Role.RoleName.valueOf(roleName);
                    Role role = roleRepository.findByName(roleEnum)
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid role name: " + roleName);
                }
            }
        }

        return roles;
    }
}

// Made with Bob