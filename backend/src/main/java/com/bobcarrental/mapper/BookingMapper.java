package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.BookingRequest;
import com.bobcarrental.dto.response.BookingResponse;
import com.bobcarrental.dto.response.BookingSummaryResponse;
import com.bobcarrental.model.Booking;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper za Booking entitet
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BookingMapper {
    
    /**
     * Mapira BookingRequest u Booking entitet (za kreiranje)
     */
    @Mapping(target = "id", ignore = true)
    Booking toEntity(BookingRequest request);
    
    /**
     * Mapira Booking entitet u BookingResponse (puni detalji)
     */
    @Mapping(target = "fullInfo", expression = "java(booking.getFullInfo())")
    BookingResponse toResponse(Booking booking);
    
    /**
     * Mapira Booking entitet u BookingSummaryResponse (sažetak)
     */
    @Mapping(target = "clientName", expression = "java(booking.getClient() != null ? booking.getClient().getClientName() : null)")
    @Mapping(target = "typeName", expression = "java(booking.getVehicleType() != null ? booking.getVehicleType().getTypeName() : null)")
    @Mapping(target = "status", expression = "java(booking.getStatus())")
    BookingSummaryResponse toSummaryResponse(Booking booking);
    
    /**
     * Mapira listu Booking entiteta u listu BookingResponse
     */
    List<BookingResponse> toResponseList(List<Booking> bookings);
    
    /**
     * Mapira listu Booking entiteta u listu BookingSummaryResponse
     */
    List<BookingSummaryResponse> toSummaryResponseList(List<Booking> bookings);
    
    /**
     * Ažurira postojeći Booking entitet iz BookingRequest (za update)
     */
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(BookingRequest request, @MappingTarget Booking booking);
}

// Made with Bob
