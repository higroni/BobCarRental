package com.bobcarrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TripSheet entity - migrated from TRPSHEET.DBF
 * Represents confirmatory orders / trip sheets
 * Most complex entity with fare calculations
 */
@Entity
@Table(name = "trip_sheets",
       uniqueConstraints = @UniqueConstraint(columnNames = "trp_num"),
       indexes = {
           @Index(name = "idx_trp_num", columnList = "trp_num"),
           @Index(name = "idx_trp_date", columnList = "trp_date"),
           @Index(name = "idx_client_id", columnList = "client_id"),
           @Index(name = "idx_bill_num", columnList = "bill_num"),
           @Index(name = "idx_is_billed", columnList = "is_billed")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripSheet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Trip sheet number (PRIMARY KEY in legacy)
     * Validated with PresenceChk
     */
    @NotNull(message = "Trip number is required")
    @Column(name = "trp_num", nullable = false, unique = true)
    private Integer trpNum;

    /**
     * Client name (denormalized for MISC clients)
     * For regular clients, copied from CLIENT table
     */
    @Size(max = 35, message = "Client name must not exceed 35 characters")
    @Column(name = "client_name", length = 35)
    private String clientName;

    /**
     * Trip sheet date
     */
    @NotNull(message = "Trip date is required")
    @Column(name = "trp_date", nullable = false)
    private LocalDate trpDate;

    /**
     * Vehicle registration number
     */
    @Size(max = 14, message = "Registration number must not exceed 14 characters")
    @Column(name = "reg_num", length = 14)
    private String regNum;

    /**
     * Starting kilometer reading
     * Must be <= endKm
     */
    @NotNull(message = "Start kilometer is required")
    @Min(value = 0, message = "Start kilometer must be >= 0")
    @Column(name = "start_km", nullable = false)
    private Integer startKm;

    /**
     * Ending kilometer reading
     * Must be >= startKm
     */
    @NotNull(message = "End kilometer is required")
    @Min(value = 0, message = "End kilometer must be >= 0")
    @Column(name = "end_km", nullable = false)
    private Integer endKm;

    /**
     * Vehicle type ID (Foreign Key -> VEHTYPE)
     */
    @NotBlank(message = "Vehicle type is required")
    @Size(max = 5, message = "Type ID must not exceed 5 characters")
    @Column(name = "type_id", nullable = false, length = 5)
    private String typeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_fk", referencedColumnName = "id")
    private VehicleType vehicleType;

    /**
     * Starting date
     * Must be <= endDt
     */
    @NotNull(message = "Start date is required")
    @Column(name = "start_dt", nullable = false)
    private LocalDate startDt;

    /**
     * Ending date
     * Must be >= startDt
     */
    @NotNull(message = "End date is required")
    @Column(name = "end_dt", nullable = false)
    private LocalDate endDt;

    /**
     * Starting time (HH:MM format)
     * Validated with CheckTime
     */
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$", 
             message = "Start time must be in HH:MM format")
    @Column(name = "start_tm", length = 5)
    private String startTm;

    /**
     * Ending time (HH:MM format)
     * Validated with CheckTime
     */
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$", 
             message = "End time must be in HH:MM format")
    @Column(name = "end_tm", length = 5)
    private String endTm;

    /**
     * Driver name
     */
    @Size(max = 25, message = "Driver name must not exceed 25 characters")
    @Column(name = "driver", length = 25)
    private String driver;

    /**
     * Client ID (Foreign Key -> CLIENT)
     * Can be "MISC" for one-time clients
     */
    @NotBlank(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    @Column(name = "client_id", nullable = false, length = 10)
    private String clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_fk", referencedColumnName = "id")
    private Client client;

    /**
     * Is this trip billed?
     */
    @Column(name = "is_billed", nullable = false)
    @Builder.Default
    private Boolean isBilled = false;

    /**
     * Bill number (Foreign Key -> BILLING)
     * Validated with BillChk (must not exist for insert)
     */
    @Column(name = "bill_num")
    private Integer billNum;

    /**
     * Bill date
     */
    @Column(name = "bill_date")
    private LocalDate billDate;

    /**
     * Trip status/type
     * F = Flat Rate (local)
     * S = Split Rate (hire + fuel)
     * O = Outstation
     */
    @Column(name = "status", length = 1)
    private String status;

    /**
     * Calculated charges (from TripProcess function)
     */
    @Column(name = "hiring", precision = 10, scale = 2)
    private BigDecimal hiring;

    @Column(name = "extra", precision = 10, scale = 2)
    private BigDecimal extra;

    @Column(name = "halt", precision = 10, scale = 2)
    private BigDecimal halt;

    @Column(name = "minimum", precision = 10, scale = 2)
    private BigDecimal minimum;

    /**
     * Total hours calculated (Time2Val function)
     */
    @Column(name = "time")
    private Integer time;

    /**
     * Total calendar days
     */
    @Column(name = "days")
    private Integer days;

    /**
     * Permit charges (manual entry)
     */
    @Column(name = "permit", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal permit = BigDecimal.ZERO;

    /**
     * Miscellaneous charges (parking, etc.)
     */
    @Column(name = "misc", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal misc = BigDecimal.ZERO;

    /**
     * Tagged for filtering/reporting
     */
    @Column(name = "tagged")
    @Builder.Default
    private Boolean tagged = false;

    /**
     * Soft delete flag
     * Legacy system didn't allow deletion
     */
    @Column(name = "deleted")
    @Builder.Default
    private Boolean deleted = false;

    // Helper methods
    public Integer getTotalKm() {
        if (endKm != null && startKm != null) {
            return endKm - startKm;
        }
        return 0;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        
        if (hiring != null) total = total.add(hiring);
        if (extra != null) total = total.add(extra);
        if (halt != null) total = total.add(halt);
        if (minimum != null) total = total.add(minimum);
        if (permit != null) total = total.add(permit);
        if (misc != null) total = total.add(misc);
        
        return total;
    }

    public boolean isFlatRate() {
        return "F".equals(status);
    }

    public boolean isSplitRate() {
        return "S".equals(status);
    }

    public boolean isOutstation() {
        return "O".equals(status);
    }

    public boolean isMiscClient() {
        return "MISC".equalsIgnoreCase(clientId);
    }

    public String getStatusDescription() {
        return switch (status) {
            case "F" -> "Flat Rate";
            case "S" -> "Split Rate";
            case "O" -> "Outstation";
            default -> "Unknown";
        };
    }

    /**
     * Alias for trpNum field - used by services
     */
    public Integer getTripNo() {
        return trpNum;
    }

    public void setTripNo(Integer tripNo) {
        this.trpNum = tripNo;
    }

    /**
     * Booking relationship - TripSheet is created from Booking
     * This is a transient field for service layer use
     */
    @Transient
    private com.bobcarrental.model.Booking booking;

    public com.bobcarrental.model.Booking getBooking() {
        return booking;
    }

    public void setBooking(com.bobcarrental.model.Booking booking) {
        this.booking = booking;
    }

    /**
     * Alias methods for date/time fields
     */
    public LocalDate getStartDate() {
        return startDt;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDt = startDate;
    }

    public LocalDate getEndDate() {
        return endDt;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDt = endDate;
    }

    public String getStartTime() {
        return startTm;
    }

    public void setStartTime(String startTime) {
        this.startTm = startTime;
    }

    public String getEndTime() {
        return endTm;
    }

    public void setEndTime(String endTime) {
        this.endTm = endTime;
    }

    /**
     * Calculated fields for split rate
     */
    public void setLocalKm(double localKm) {
        // This is a calculated field, stored in hiring
    }

    public void setOutKm(double outKm) {
        // This is a calculated field, stored in extra
    }

    public void setLocalAmount(double localAmount) {
        // This is a calculated field, stored in hiring
    }

    public void setOutAmount(double outAmount) {
        // This is a calculated field, stored in extra
    }

    public double getLocalAmount() {
        return hiring != null ? hiring.doubleValue() : 0.0;
    }

    public double getOutAmount() {
        return extra != null ? extra.doubleValue() : 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripSheet)) return false;
        TripSheet that = (TripSheet) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TripSheet{" +
                "id=" + id +
                ", trpNum=" + trpNum +
                ", trpDate=" + trpDate +
                ", clientId='" + clientId + '\'' +
                ", typeId='" + typeId + '\'' +
                ", status='" + status + '\'' +
                ", totalKm=" + getTotalKm() +
                ", totalAmount=" + getTotalAmount() +
                ", isBilled=" + isBilled +
                ", billNum=" + billNum +
                '}';
    }
}

// Made with Bob
