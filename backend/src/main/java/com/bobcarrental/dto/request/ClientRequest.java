package com.bobcarrental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO za kreiranje/ažuriranje klijenta
 * Mapira se na Client entitet
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
    
    @NotBlank(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Client ID must be uppercase alphanumeric")
    private String clientId;
    
    @NotBlank(message = "Client name is required")
    @Size(max = 40, message = "Client name must not exceed 40 characters")
    private String clientName;
    
    @Size(max = 35, message = "Address1 must not exceed 35 characters")
    private String address1;
    
    @Size(max = 30, message = "Address2 must not exceed 30 characters")
    private String address2;
    
    @Size(max = 25, message = "Address3 must not exceed 25 characters")
    private String address3;
    
    @Size(max = 20, message = "Place must not exceed 20 characters")
    private String place;
    
    @Size(max = 15, message = "City must not exceed 15 characters")
    private String city;
    
    @Size(max = 6, message = "Pin code must not exceed 6 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Pin code must contain only digits")
    private String pinCode;
    
    @Size(max = 25, message = "Phone must not exceed 25 characters")
    private String phone;
    
    @Size(max = 25, message = "Fax must not exceed 25 characters")
    private String fax;
    
    /**
     * Cenovnik (MEMO polje iz legacy sistema)
     */
    private String fare;
    
    /**
     * Dozvola za split rate tarifu
     */
    @Builder.Default
    private Boolean isSplit = false;
    
    /**
     * Oznaka za filtriranje
     */
    @Builder.Default
    private Boolean tagged = false;

    /**
     * Helper method to get name (alias for clientName)
     */
    public String getName() {
        return clientName;
    }
}

// Made with Bob
