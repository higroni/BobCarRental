package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.AddressRequest;
import com.bobcarrental.dto.response.AddressResponse;
import com.bobcarrental.model.Address;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Address entity
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AddressMapper {
    
    @Mapping(target = "id", ignore = true)
    Address toEntity(AddressRequest request);
    
    AddressResponse toResponse(Address entity);
    
    List<AddressResponse> toResponseList(List<Address> entities);
    
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(AddressRequest request, @MappingTarget Address entity);
}

// Made with Bob
