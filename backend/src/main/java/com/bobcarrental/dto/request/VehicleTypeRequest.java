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
public class VehicleTypeRequest {
    @NotBlank(message = "Type ID is required")
    @Size(max = 5)
    private String typeId;
    
    @NotBlank(message = "Type name is required")
    @Size(max = 30)
    private String typeName;
    
    @Size(max = 100)
    private String description;
    
    @Builder.Default
    private Boolean isActive = true;
}

// Made with Bob
