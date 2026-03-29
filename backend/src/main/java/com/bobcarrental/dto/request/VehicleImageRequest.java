package com.bobcarrental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleImageRequest {
    @NotNull(message = "Vehicle type ID is required")
    private Long vehicleTypeId;
    
    @NotBlank(message = "Image name is required")
    @Size(max = 255)
    private String imageName;
    
    @NotBlank(message = "Content type is required")
    @Size(max = 50)
    private String contentType;
    
    private Long fileSize;
    
    @Builder.Default
    private Integer displayOrder = 0;
    
    @Builder.Default
    private Boolean isPrimary = false;
    
    @Size(max = 500)
    private String description;
}

// Made with Bob
