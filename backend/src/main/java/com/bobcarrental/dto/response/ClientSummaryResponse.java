package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Client entity (summary for lists)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSummaryResponse {
    
    private Long id;
    private String clientId;
    private String clientName;
    private String city;
    private String phone;
    private String mobile;
    private Boolean isActive;
}

// Made with Bob