package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO za putni list (sažetak)
 * Koristi se za liste putnih listova (GET /tripsheets)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripSheetSummaryResponse {
    
    private Long id;
    private Integer trpNum;
    private String clientName;
    private LocalDate trpDate;
    private String regNum;
    private String typeId;
    private String clientId;
    private Integer totalKm;
    private String status;
    private BigDecimal totalAmount;
    private Boolean isBilled;
    private Boolean tagged;
}

// Made with Bob
