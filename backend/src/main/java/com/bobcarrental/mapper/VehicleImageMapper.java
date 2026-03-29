package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.VehicleImageRequest;
import com.bobcarrental.dto.response.VehicleImageResponse;
import com.bobcarrental.model.VehicleImage;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for VehicleImage entity
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VehicleImageMapper {
    
    @Mapping(target = "id", ignore = true)
    VehicleImage toEntity(VehicleImageRequest request);
    
    VehicleImageResponse toResponse(VehicleImage entity);
    
    List<VehicleImageResponse> toResponseList(List<VehicleImage> entities);
    
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(VehicleImageRequest request, @MappingTarget VehicleImage entity);
}

// Made with Bob
