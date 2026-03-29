package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.BillingRequest;
import com.bobcarrental.dto.response.BillingResponse;
import com.bobcarrental.dto.response.BillingSummaryResponse;
import com.bobcarrental.model.Billing;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Billing entity
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BillingMapper {
    
    @Mapping(target = "id", ignore = true)
    Billing toEntity(BillingRequest request);
    
    BillingResponse toResponse(Billing entity);
    
    BillingSummaryResponse toSummaryResponse(Billing entity);
    
    List<BillingResponse> toResponseList(List<Billing> entities);
    
    List<BillingSummaryResponse> toSummaryResponseList(List<Billing> entities);
    
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(BillingRequest request, @MappingTarget Billing entity);
}

// Made with Bob
