package com.bobcarrental.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardFareRequest {
    @NotBlank(message = "Vehicle code is required")
    @Size(max = 5)
    @Pattern(regexp = "^[A-Z0-9]+$")
    private String vehicleCode;
    
    @NotNull(message = "Fare type is required")
    private String fareType;
    
    @Min(0)
    private Integer hours;
    
    // Rate is optional - different fare types use different fields
    // LOCAL/EXTRA use rate, GENERAL uses fuelRatePerKm/ratePerExcessKm, OUTSTATION uses ratePerKm
    @DecimalMin("0.0")
    private BigDecimal rate;
    
    @Min(0)
    private Integer freeKm;
    
    @DecimalMin("0.0")
    private BigDecimal splitFare;
    
    @Min(0)
    private Integer splitFuelKm;
    
    @DecimalMin("0.0")
    private BigDecimal hireRate;
    
    @Min(0)
    private Integer hireKm;
    
    @DecimalMin("0.0")
    private BigDecimal fuelRatePerKm;
    
    @DecimalMin("0.0")
    private BigDecimal ratePerExcessKm;
    
    @DecimalMin("0.0")
    private BigDecimal ratePerKm;
    
    @Min(0)
    private Integer minKmPerDay;
    
    @DecimalMin("0.0")
    private BigDecimal nightHalt;
    
    @Builder.Default
    private Boolean active = true;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String description;
}

// Made with Bob
