package com.bobcarrental.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO za kreiranje/ažuriranje putnog lista
 * Mapira se na TripSheet entitet
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripSheetRequest {
    
    @NotNull(message = "Trip number is required")
    @Min(value = 1, message = "Trip number must be positive")
    private Integer trpNum;
    
    @NotNull(message = "Trip date is required")
    private LocalDate trpDate;
    
    @NotBlank(message = "Vehicle ID is required")
    @Size(max = 14, message = "Vehicle ID must not exceed 14 characters")
    private String regNum;
    
    @Size(max = 25, message = "Driver name must not exceed 25 characters")
    private String driver;
    
    @Size(max = 20, message = "Booking ID must not exceed 20 characters")
    private String bookingId;
    
    @NotBlank(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    private String clientId;
    
    @NotNull(message = "Start kilometer is required")
    @Min(value = 0, message = "Start kilometer must be non-negative")
    private Integer startKm;
    
    @NotNull(message = "End kilometer is required")
    @Min(value = 0, message = "End kilometer must be non-negative")
    private Integer endKm;
    
    @NotBlank(message = "Vehicle type is required")
    @Size(max = 5, message = "Type ID must not exceed 5 characters")
    private String typeId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$", 
             message = "Start time must be in HH:MM format")
    private String startTime;
    
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$", 
             message = "End time must be in HH:MM format")
    private String endTime;
    
    @Builder.Default
    private Boolean isBilled = false;
    
    private Integer billNumber;
    
    private LocalDate billDate;
    
    @Pattern(regexp = "^[FSO]$", message = "Status must be F (Flat), S (Split), or O (Outstation)")
    private String status;
    
    @DecimalMin(value = "0.0", message = "Hiring must be non-negative")
    @Builder.Default
    private BigDecimal hiring = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Extra must be non-negative")
    @Builder.Default
    private BigDecimal extra = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Halt must be non-negative")
    @Builder.Default
    private BigDecimal halt = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Minimum must be non-negative")
    @Builder.Default
    private BigDecimal minimum = BigDecimal.ZERO;
    
    @Min(value = 0, message = "Time must be non-negative")
    @Builder.Default
    private Integer time = 0;
    
    @Min(value = 0, message = "Days must be non-negative")
    @Builder.Default
    private Integer days = 0;
    
    @DecimalMin(value = "0.0", message = "Permit must be non-negative")
    @Builder.Default
    private BigDecimal permit = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Misc must be non-negative")
    @Builder.Default
    private BigDecimal misc = BigDecimal.ZERO;
    
    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    private String remarks;
    
    @Builder.Default
    private Boolean tagged = false;
}

// Made with Bob
