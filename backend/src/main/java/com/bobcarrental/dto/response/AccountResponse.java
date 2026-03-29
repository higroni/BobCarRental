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
public class AccountResponse {
    private Long id;
    private String desc;
    private Long num;
    private LocalDate date;
    private String clientId;
    private BigDecimal recd;
    private BigDecimal bill;
    private Boolean tagged;
    private String code;
    private String accountType;
    private String name;
    private BigDecimal currentBalance;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Made with Bob
