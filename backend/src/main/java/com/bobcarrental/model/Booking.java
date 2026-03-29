package com.bobcarrental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Booking entity - migrated from BOOKING.DBF
 * Represents vehicle reservations/bookings
 */
@Entity
@Table(name = "bookings",
       indexes = {
           @Index(name = "idx_booking_date", columnList = "book_date"),
           @Index(name = "idx_today_date", columnList = "today_date"),
           @Index(name = "idx_client_id", columnList = "client_id"),
           @Index(name = "idx_type_id", columnList = "type_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reporting/execution date - when the booking is scheduled
     * PRIMARY KEY in legacy system
     */
    @NotNull(message = "Booking date is required")
    @Column(name = "book_date", nullable = false)
    private LocalDate bookDate;

    /**
     * Order/creation date - when the booking was created
     */
    @NotNull(message = "Today date is required")
    @Column(name = "today_date", nullable = false)
    private LocalDate todayDate;

    /**
     * Reference number/code
     */
    @Size(max = 15, message = "Reference must not exceed 15 characters")
    @Column(name = "ref", length = 15)
    private String ref;

    /**
     * Reporting time (HH:MM format)
     * Validated with CheckTime function
     */
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$", 
             message = "Time must be in HH:MM format (00:00-24:59)")
    @Column(name = "time", length = 5)
    private String time;

    /**
     * Vehicle type ID (Foreign Key -> VEHTYPE)
     * Validated with SuperCheckIt function
     */
    @NotBlank(message = "Vehicle type is required")
    @Size(max = 5, message = "Type ID must not exceed 5 characters")
    @Column(name = "type_id", nullable = false, length = 5)
    private String typeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_fk", referencedColumnName = "id")
    private VehicleType vehicleType;

    /**
     * Client ID (Foreign Key -> CLIENT)
     * Validated with SuperCheckIt function
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
     * Information/Address lines (4 lines)
     * Used for pickup/dropoff locations or special instructions
     */
    @Size(max = 50, message = "Info1 must not exceed 50 characters")
    @Column(name = "info1", length = 50)
    private String info1;

    @Size(max = 50, message = "Info2 must not exceed 50 characters")
    @Column(name = "info2", length = 50)
    private String info2;

    @Size(max = 50, message = "Info3 must not exceed 50 characters")
    @Column(name = "info3", length = 50)
    private String info3;

    @Size(max = 50, message = "Info4 must not exceed 50 characters")
    @Column(name = "info4", length = 50)
    private String info4;

    /**
     * Tagged for filtering/reporting
     * Legacy feature from DBF system
     */
    @Column(name = "tagged")
    @Builder.Default
    private Boolean tagged = false;

    /**
     * Soft delete flag
     * Legacy system allowed deletion with PACK command
     */
    @Column(name = "deleted")
    @Builder.Default
    private Boolean deleted = false;

    // Helper methods
    public String getFullInfo() {
        StringBuilder sb = new StringBuilder();
        if (info1 != null && !info1.trim().isEmpty()) {
            sb.append(info1);
        }
        if (info2 != null && !info2.trim().isEmpty()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(info2);
        }
        if (info3 != null && !info3.trim().isEmpty()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(info3);
        }
        if (info4 != null && !info4.trim().isEmpty()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(info4);
        }
        return sb.toString();
    }

    public boolean isMiscClient() {
        return "MISC".equalsIgnoreCase(clientId);
    }

    public LocalTime getTimeAsLocalTime() {
        if (time != null && time.matches("^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$")) {
            return LocalTime.parse(time);
        }
        return null;
    }

    /**
     * Booking status field - added for modern workflow
     * PENDING -> CONFIRMED -> IN_PROGRESS -> COMPLETED
     * Can be CANCELLED at any stage
     */
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "PENDING";

    public String getStatus() {
        // If deleted flag is set (legacy cancellation), override status
        if (deleted != null && deleted) {
            return "CANCELLED";
        }
        // Return actual status or default to PENDING
        return status != null ? status : "PENDING";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Booking number - generated from ref or id
     */
    public String getBookingNo() {
        if (ref != null && !ref.trim().isEmpty()) {
            return ref;
        }
        return id != null ? "BK" + String.format("%06d", id) : null;
    }

    /**
     * Alias methods for service compatibility
     */
    public LocalDate getFromDate() {
        return bookDate;
    }

    public LocalDate getToDate() {
        return bookDate; // For single-day bookings, from and to are the same
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return id != null && id.equals(booking.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", bookDate=" + bookDate +
                ", todayDate=" + todayDate +
                ", ref='" + ref + '\'' +
                ", time='" + time + '\'' +
                ", typeId='" + typeId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}

// Made with Bob
