package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Client entity (full details)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    
    private Long id;
    private String clientId;
    private String clientName;
    private String address;
    private String city;
    private String phone;
    private String mobile;
    private String email;
    private String gstNumber;
    private String panNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Made with Bob