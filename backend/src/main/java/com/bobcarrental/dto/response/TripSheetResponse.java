package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO za putni list (puni detalji)
 * Koristi se za GET /tripsheets/{id}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripSheetResponse {
    
    private Long id;
    private Integer trpNum;
    private String clientName;
    private LocalDate trpDate;
    private String regNum;
    private String driver;
    private String clientId;
    private Integer startKm;
    private Integer endKm;
    private Integer totalKm;
    private String typeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String startTime;
    private String endTime;
    private Boolean isBilled;
    private Integer billNumber;
    private LocalDate billDate;
    private String status;
    private String statusDescription;
    private BigDecimal hiring;
    private BigDecimal extra;
    private BigDecimal halt;
    private BigDecimal minimum;
    private Integer time;
    private Integer days;
    private BigDecimal permit;
    private BigDecimal misc;
    private BigDecimal totalAmount;
    private String remarks;
    private Boolean tagged;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Made with Bob
