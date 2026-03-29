package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeSummaryResponse {
    private Long id;
    private String typeId;
    private String typeName;
    private Boolean isActive;
}

// Made with Bob
