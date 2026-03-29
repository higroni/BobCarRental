package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.TripSheetRequest;
import com.bobcarrental.dto.response.TripSheetResponse;
import com.bobcarrental.dto.response.TripSheetSummaryResponse;
import com.bobcarrental.model.TripSheet;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper za TripSheet entitet
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TripSheetMapper {
    
    /**
     * Mapira TripSheetRequest u TripSheet entitet (za kreiranje)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "startDate", target = "startDt")
    @Mapping(source = "endDate", target = "endDt")
    @Mapping(source = "startTime", target = "startTm")
    @Mapping(source = "endTime", target = "endTm")
    TripSheet toEntity(TripSheetRequest request);
    
    /**
     * Mapira TripSheet entitet u TripSheetResponse (puni detalji)
     */
    @Mapping(source = "startDt", target = "startDate")
    @Mapping(source = "endDt", target = "endDate")
    @Mapping(source = "startTm", target = "startTime")
    @Mapping(source = "endTm", target = "endTime")
    @Mapping(source = "billNum", target = "billNumber")
    @Mapping(target = "totalKm", expression = "java(tripSheet.getTotalKm())")
    @Mapping(target = "totalAmount", expression = "java(tripSheet.getTotalAmount())")
    @Mapping(target = "statusDescription", expression = "java(tripSheet.getStatusDescription())")
    TripSheetResponse toResponse(TripSheet tripSheet);
    
    /**
     * Mapira TripSheet entitet u TripSheetSummaryResponse (sažetak)
     */
    @Mapping(target = "totalKm", expression = "java(tripSheet.getTotalKm())")
    @Mapping(target = "totalAmount", expression = "java(tripSheet.getTotalAmount())")
    TripSheetSummaryResponse toSummaryResponse(TripSheet tripSheet);
    
    /**
     * Mapira listu TripSheet entiteta u listu TripSheetResponse
     */
    List<TripSheetResponse> toResponseList(List<TripSheet> tripSheets);
    
    /**
     * Mapira listu TripSheet entiteta u listu TripSheetSummaryResponse
     */
    List<TripSheetSummaryResponse> toSummaryResponseList(List<TripSheet> tripSheets);
    
    /**
     * Ažurira postojeći TripSheet entitet iz TripSheetRequest (za update)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "startDate", target = "startDt")
    @Mapping(source = "endDate", target = "endDt")
    @Mapping(source = "startTime", target = "startTm")
    @Mapping(source = "endTime", target = "endTm")
    void updateEntityFromRequest(TripSheetRequest request, @MappingTarget TripSheet tripSheet);
}

// Made with Bob
