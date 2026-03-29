package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardFareResponse {
    private Long id;
    private String vehicleCode;
    private String fareType;
    private Integer hours;
    private BigDecimal rate;
    private Integer freeKm;
    private BigDecimal splitFare;
    private Integer splitFuelKm;
    private BigDecimal hireRate;
    private Integer hireKm;
    private BigDecimal fuelRatePerKm;
    private BigDecimal ratePerExcessKm;
    private BigDecimal ratePerKm;
    private Integer minKmPerDay;
    private BigDecimal nightHalt;
    private Boolean active;
    private Boolean isActive;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Made with Bob
