package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Response DTO za rezervaciju (sažetak)
 * Koristi se za liste rezervacija (GET /bookings)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSummaryResponse {
    
    private Long id;
    private LocalDate bookDate;
    private String time;
    private String ref;
    private String typeId;
    private String typeName;        // Vehicle type name for display
    private String clientId;
    private String clientName;      // Client name for display
    private String info1;
    private Boolean tagged;
    private String status;          // Booking status (PENDING, CONFIRMED, etc.)
}

// Made with Bob
