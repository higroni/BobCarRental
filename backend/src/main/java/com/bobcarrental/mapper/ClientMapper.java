package com.bobcarrental.mapper;

import com.bobcarrental.dto.request.ClientRequest;
import com.bobcarrental.dto.response.ClientResponse;
import com.bobcarrental.dto.response.ClientSummaryResponse;
import com.bobcarrental.model.Client;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper za Client entitet
 * Automatski generiše kod za mapiranje između entiteta i DTO-ova
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ClientMapper {
    
    /**
     * Mapira ClientRequest u Client entitet (za kreiranje)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address1", qualifiedByName = "emptyStringToNull")
    @Mapping(target = "address2", qualifiedByName = "emptyStringToNull")
    @Mapping(target = "address3", qualifiedByName = "emptyStringToNull")
    @Mapping(target = "place", qualifiedByName = "emptyStringToNull")
    @Mapping(target = "city", qualifiedByName = "emptyStringToNull")
    @Mapping(target = "pinCode", qualifiedByName = "emptyStringToNull")
    @Mapping(target = "phone", qualifiedByName = "emptyStringToNull")
    @Mapping(target = "fax", qualifiedByName = "emptyStringToNull")
    @Mapping(target = "fare", qualifiedByName = "emptyStringToNull")
    Client toEntity(ClientRequest request);
    
    /**
     * Mapira Client entitet u ClientResponse (puni detalji)
     */
    ClientResponse toResponse(Client client);
    
    /**
     * Mapira Client entitet u ClientSummaryResponse (sažetak)
     */
    ClientSummaryResponse toSummaryResponse(Client client);
    
    /**
     * Mapira listu Client entiteta u listu ClientResponse
     */
    List<ClientResponse> toResponseList(List<Client> clients);
    
    /**
     * Mapira listu Client entiteta u listu ClientSummaryResponse
     */
    List<ClientSummaryResponse> toSummaryResponseList(List<Client> clients);
    
    /**
     * Ažurira postojeći Client entitet iz ClientRequest (za update)
     * Ignoriše null vrednosti iz request-a
     */
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(ClientRequest request, @MappingTarget Client client);
    
    /**
     * Konvertuje prazan string u null
     * Koristi se za polja koja mogu biti prazna
     */
    @Named("emptyStringToNull")
    default String emptyStringToNull(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value;
    }
}

// Made with Bob
