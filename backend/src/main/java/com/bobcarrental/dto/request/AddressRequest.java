package com.bobcarrental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    @NotBlank(message = "Client ID is required")
    @Size(max = 10)
    private String clientId;
    
    @Size(max = 15)
    private String dept;
    
    @Size(max = 10)
    private String desc;
    
    @NotBlank(message = "Name is required")
    @Size(max = 40)
    private String name;
    
    @Size(max = 35)
    private String address1;
    
    @Size(max = 30)
    private String address2;
    
    @Size(max = 25)
    private String address3;
    
    @Size(max = 20)
    private String place;
    
    @Size(max = 15)
    private String city;
    
    private Integer pinCode;
    
    @Size(max = 25)
    private String phone;
    
    @Size(max = 25)
    private String fax;
    
    @Size(max = 20)
    private String category;
    
    @Size(max = 100)
    private String companyName;
    
    @Builder.Default
    private Boolean isActive = true;
}

// Made with Bob
