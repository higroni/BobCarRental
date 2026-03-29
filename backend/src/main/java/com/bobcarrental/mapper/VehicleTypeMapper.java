package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.VehicleTypeRequest;
import com.bobcarrental.dto.response.VehicleTypeResponse;
import com.bobcarrental.dto.response.VehicleTypeSummaryResponse;
import com.bobcarrental.model.VehicleType;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for VehicleType entity
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VehicleTypeMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "typeDesc", source = "typeName")
    @Mapping(target = "deleted", expression = "java(!request.getIsActive())")
    VehicleType toEntity(VehicleTypeRequest request);
    
    @Mapping(target = "typeName", source = "typeDesc")
    @Mapping(target = "isActive", expression = "java(!entity.getDeleted())")
    VehicleTypeResponse toResponse(VehicleType entity);
    
    @Mapping(target = "typeName", source = "typeDesc")
    @Mapping(target = "isActive", expression = "java(!entity.getDeleted())")
    VehicleTypeSummaryResponse toSummaryResponse(VehicleType entity);
    
    List<VehicleTypeResponse> toResponseList(List<VehicleType> entities);
    
    List<VehicleTypeSummaryResponse> toSummaryResponseList(List<VehicleType> entities);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "typeDesc", source = "typeName")
    @Mapping(target = "deleted", expression = "java(!request.getIsActive())")
    void updateEntityFromRequest(VehicleTypeRequest request, @MappingTarget VehicleType entity);
}

// Made with Bob
