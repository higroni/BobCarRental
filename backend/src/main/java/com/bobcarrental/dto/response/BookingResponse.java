package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO za rezervaciju (puni detalji)
 * Koristi se za GET /bookings/{id}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    
    private Long id;
    private LocalDate bookDate;
    private LocalDate todayDate;
    private String time;
    private String ref;
    private String typeId;
    private String clientId;
    private String info1;
    private String info2;
    private String info3;
    private String info4;
    private Boolean tagged;
    private String fullInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Made with Bob
