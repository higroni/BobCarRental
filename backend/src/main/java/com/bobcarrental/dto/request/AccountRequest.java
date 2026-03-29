package com.bobcarrental.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for Account entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    
    @NotBlank(message = "Description is required")
    @Size(max = 15, message = "Description must not exceed 15 characters")
    private String desc;
    
    private Long num;
    
    @NotNull(message = "Date is required")
    private java.time.LocalDate date;
    
    @NotBlank(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    private String clientId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", message = "Amount must be non-negative")
    private BigDecimal amount;
    
    @Builder.Default
    private Boolean tagged = false;
}

// Made with Bob