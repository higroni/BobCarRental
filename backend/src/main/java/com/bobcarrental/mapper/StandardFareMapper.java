package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.StandardFareRequest;
import com.bobcarrental.dto.response.StandardFareResponse;
import com.bobcarrental.model.StandardFare;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for StandardFare entity
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StandardFareMapper {
    
    @Mapping(target = "id", ignore = true)
    StandardFare toEntity(StandardFareRequest request);
    
    StandardFareResponse toResponse(StandardFare entity);
    
    List<StandardFareResponse> toResponseList(List<StandardFare> entities);
    
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(StandardFareRequest request, @MappingTarget StandardFare entity);
}

// Made with Bob
