package com.bobcarrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * StandardFare entity - migrated from FARES.TXT
 * Represents standard fare/price lists
 * ADMIN ONLY access - replaces text file with database CRUD
 */
@Entity
@Table(name = "standard_fares",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"vehicle_code", "fare_type", "hours"}
       ),
       indexes = {
           @Index(name = "idx_vehicle_code", columnList = "vehicle_code"),
           @Index(name = "idx_fare_type", columnList = "fare_type"),
           @Index(name = "idx_active", columnList = "active")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StandardFare extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Vehicle code (e.g., AMB, CON)
     * Maps to VehicleType.typeId
     */
    @NotBlank(message = "Vehicle code is required")
    @Size(max = 5, message = "Vehicle code must not exceed 5 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Vehicle code must be uppercase alphanumeric")
    @Column(name = "vehicle_code", nullable = false, length = 5)
    private String vehicleCode;

    /**
     * Vehicle type relationship
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_fk", referencedColumnName = "id")
    private VehicleType vehicleType;

    /**
     * Fare type: LOCAL, EXTRA, GENERAL, OUTSTATION
     */
    @NotNull(message = "Fare type is required")
    @Column(name = "fare_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private FareType fareType;

    /**
     * Hours (for LOCAL and EXTRA types)
     * NULL for GENERAL and OUTSTATION
     */
    @Min(value = 0, message = "Hours must be >= 0")
    @Column(name = "hours")
    private Integer hours;

    /**
     * Rate/Basic charge (for LOCAL and EXTRA types)
     * NULL for GENERAL and OUTSTATION types
     */
    @DecimalMin(value = "0.0", message = "Rate must be >= 0")
    @Column(name = "rate", precision = 10, scale = 2)
    private BigDecimal rate;

    /**
     * Free kilometers (for LOCAL and EXTRA)
     */
    @Min(value = 0, message = "Free KM must be >= 0")
    @Column(name = "free_km")
    private Integer freeKm;

    /**
     * Split fare/Hire rate (for LOCAL and EXTRA)
     */
    @DecimalMin(value = "0.0", message = "Split fare must be >= 0")
    @Column(name = "split_fare", precision = 10, scale = 2)
    private BigDecimal splitFare;

    /**
     * Split fuel KM rate (for LOCAL and EXTRA)
     */
    @Min(value = 0, message = "Split fuel KM must be >= 0")
    @Column(name = "split_fuel_km")
    private Integer splitFuelKm;

    /**
     * Hire rate (for EXTRA type)
     */
    @DecimalMin(value = "0.0", message = "Hire rate must be >= 0")
    @Column(name = "hire_rate", precision = 10, scale = 2)
    private BigDecimal hireRate;

    /**
     * Hire KM (for EXTRA type)
     */
    @Min(value = 0, message = "Hire KM must be >= 0")
    @Column(name = "hire_km")
    private Integer hireKm;

    /**
     * Fuel rate per KM (for GENERAL type)
     */
    @DecimalMin(value = "0.0", message = "Fuel rate must be >= 0")
    @Column(name = "fuel_rate_per_km", precision = 10, scale = 2)
    private BigDecimal fuelRatePerKm;

    /**
     * Rate per excess KM (for GENERAL type)
     */
    @DecimalMin(value = "0.0", message = "Rate per excess KM must be >= 0")
    @Column(name = "rate_per_excess_km", precision = 10, scale = 2)
    private BigDecimal ratePerExcessKm;

    /**
     * Rate per KM (for OUTSTATION type)
     */
    @DecimalMin(value = "0.0", message = "Rate per KM must be >= 0")
    @Column(name = "rate_per_km", precision = 10, scale = 2)
    private BigDecimal ratePerKm;

    /**
     * Minimum KM per calendar day (for OUTSTATION type)
     */
    @Min(value = 0, message = "Min KM per day must be >= 0")
    @Column(name = "min_km_per_day")
    private Integer minKmPerDay;

    /**
     * Night halt charge (for OUTSTATION type)
     */
    @DecimalMin(value = "0.0", message = "Night halt must be >= 0")
    @Column(name = "night_halt", precision = 10, scale = 2)
    private BigDecimal nightHalt;

    /**
     * Is this fare active?
     * Allows versioning of fares
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Alternative active flag name for compatibility
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Effective from date
     */
    @Column(name = "effective_from")
    private java.time.LocalDate effectiveFrom;

    /**
     * Effective to date
     */
    @Column(name = "effective_to")
    private java.time.LocalDate effectiveTo;

    @Column(name = "description", length = 200)
    private String description;

    public enum FareType {
        LOCAL,      // Hourly rates with free KM
        EXTRA,      // Extra hour rates
        GENERAL,    // Fuel and excess KM rates
        OUTSTATION  // Per KM rates for outstation trips
    }

    // Helper methods
    public boolean isLocal() {
        return FareType.LOCAL.equals(fareType);
    }

    public boolean isExtra() {
        return FareType.EXTRA.equals(fareType);
    }

    public boolean isGeneral() {
        return FareType.GENERAL.equals(fareType);
    }

    public boolean isOutstation() {
        return FareType.OUTSTATION.equals(fareType);
    }

    public boolean isCurrentlyActive() {
        if (!active) return false;
        
        java.time.LocalDate now = java.time.LocalDate.now();
        
        if (effectiveFrom != null && now.isBefore(effectiveFrom)) {
            return false;
        }
        
        if (effectiveTo != null && now.isAfter(effectiveTo)) {
            return false;
        }
        
        return true;
    }

    /**
     * Alias methods for FareCalculationService compatibility
     */
    public Integer getBaseKm() {
        return freeKm;
    }

    public BigDecimal getBaseAmount() {
        return rate;
    }

    public BigDecimal getExtraKmRate() {
        return ratePerExcessKm != null ? ratePerExcessKm : ratePerKm;
    }

    public BigDecimal getDriverAllowance() {
        return nightHalt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StandardFare)) return false;
        StandardFare that = (StandardFare) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "StandardFare{" +
                "id=" + id +
                ", vehicleCode='" + vehicleCode + '\'' +
                ", fareType=" + fareType +
                ", hours=" + hours +
                ", rate=" + rate +
                ", active=" + active +
                '}';
    }
}

// Made with Bob
