package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String clientId;
    private String dept;
    private String desc;
    private String name;
    private String address1;
    private String address2;
    private String address3;
    private String place;
    private String city;
    private Integer pinCode;
    private String phone;
    private String fax;
    private Boolean tagged;
    private String category;
    private String companyName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Made with Bob
