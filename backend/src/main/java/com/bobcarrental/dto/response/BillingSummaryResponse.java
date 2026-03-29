package com.bobcarrental.dto.response;

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
public class BillingSummaryResponse {
    private Long id;
    private Integer billNum;
    private LocalDate billDate;
    private String clientId;
    private BigDecimal totalAmount;
    private BigDecimal paid;
    private Boolean tagged;
}

// Made with Bob
