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
public class VehicleImageResponse {
    private Long id;
    private Long vehicleTypeId;
    private String imageName;
    private String contentType;
    private Long fileSize;
    private Long thumbnailSize;
    private LocalDateTime uploadDate;
    private Integer width;
    private Integer height;
    private Integer thumbnailWidth;
    private Integer thumbnailHeight;
    private Integer displayOrder;
    private Boolean isPrimary;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Made with Bob
