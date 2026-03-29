package com.bobcarrental.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO za kreiranje/ažuriranje računa
 * Mapira se na Billing entitet
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingRequest {
    
    @NotNull(message = "Bill number is required")
    @Min(value = 1, message = "Bill number must be positive")
    private Integer billNum;
    
    @NotNull(message = "Bill date is required")
    private LocalDate billDate;
    
    @NotBlank(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    private String clientId;
    
    private Integer trpNum;
    
    @Builder.Default
    private Boolean printed = false;
    
    @Builder.Default
    private Boolean cancelled = false;
    
    @NotNull(message = "Bill amount is required")
    @DecimalMin(value = "0.0", message = "Bill amount must be non-negative")
    private BigDecimal billAmount;
    
    /**
     * Generisani tekst računa (MEMO polje iz legacy sistema)
     */
    private String billImg;
    
    @Builder.Default
    private Boolean tagged = false;
}

// Made with Bob
