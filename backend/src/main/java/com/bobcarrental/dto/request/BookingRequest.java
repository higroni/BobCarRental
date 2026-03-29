package com.bobcarrental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO za kreiranje/ažuriranje rezervacije
 * Mapira se na Booking entitet
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    
    @NotNull(message = "Booking date is required")
    private LocalDate bookDate;
    
    @NotNull(message = "Today date is required")
    @Builder.Default
    private LocalDate todayDate = LocalDate.now();
    
    @NotBlank(message = "Time is required")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-4]):[0-5][0-9]$", 
             message = "Time must be in HH:MM format (00:00-24:59)")
    private String time;
    
    @Size(max = 15, message = "Reference must not exceed 15 characters")
    private String ref;
    
    @NotBlank(message = "Vehicle type is required")
    @Size(max = 5, message = "Type ID must not exceed 5 characters")
    private String typeId;
    
    @NotBlank(message = "Client ID is required")
    @Size(max = 10, message = "Client ID must not exceed 10 characters")
    private String clientId;
    
    @Size(max = 50, message = "Info1 must not exceed 50 characters")
    private String info1;
    
    @Size(max = 50, message = "Info2 must not exceed 50 characters")
    private String info2;
    
    @Size(max = 50, message = "Info3 must not exceed 50 characters")
    private String info3;
    
    @Size(max = 50, message = "Info4 must not exceed 50 characters")
    private String info4;
    
    @Builder.Default
    private Boolean tagged = false;
}

// Made with Bob
