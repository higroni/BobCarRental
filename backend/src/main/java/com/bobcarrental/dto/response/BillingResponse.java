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
public class BillingResponse {
    private Long id;
    private Integer billNo;
    private LocalDate billDate;
    private String clientId;
    private BigDecimal totalAmount;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal igst;
    private BigDecimal paid;
    private String remarks;
    private Boolean tagged;
    private String billImg;  // Generated bill content
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Made with Bob
