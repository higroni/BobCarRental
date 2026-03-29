package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO za autentifikaciju
 * Vraća se nakon uspešnog logina
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    /**
     * JWT access token
     */
    private String accessToken;
    
    /**
     * JWT refresh token
     */
    private String refreshToken;
    
    /**
     * Tip tokena (uvek "Bearer")
     */
    @Builder.Default
    private String tokenType = "Bearer";
    
    /**
     * Vreme isteka tokena u sekundama
     */
    private Long expiresIn;
    
    /**
     * Informacije o korisniku
     */
    private UserInfo user;
    
    /**
     * Timestamp kada je token kreiran
     */
    private LocalDateTime timestamp;
    
    /**
     * Nested class za informacije o korisniku
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private Set<String> roles;
        private boolean enabled;
    }
}

// Made with Bob
