package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.HeaderTemplateRequest;
import com.bobcarrental.dto.response.HeaderTemplateResponse;
import com.bobcarrental.model.HeaderTemplate;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for HeaderTemplate entity
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface HeaderTemplateMapper {
    
    /**
     * Convert HeaderTemplateRequest to HeaderTemplate entity
     */
    @Mapping(target = "id", ignore = true)
    HeaderTemplate toEntity(HeaderTemplateRequest request);
    
    /**
     * Convert HeaderTemplate entity to HeaderTemplateResponse
     */
    HeaderTemplateResponse toResponse(HeaderTemplate headerTemplate);
    
    /**
     * Convert list of HeaderTemplate entities to list of HeaderTemplateResponse
     */
    List<HeaderTemplateResponse> toResponseList(List<HeaderTemplate> headerTemplates);
    
    /**
     * Update existing HeaderTemplate entity from HeaderTemplateRequest
     */
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(HeaderTemplateRequest request, @MappingTarget HeaderTemplate headerTemplate);
}

// Made with Bob