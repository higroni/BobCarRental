package com.bobcarrental.service;

import com.bobcarrental.dto.request.VehicleTypeRequest;
import com.bobcarrental.dto.response.VehicleTypeResponse;
import com.bobcarrental.dto.response.VehicleTypeSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for VehicleType management
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
public interface VehicleTypeService {

    /**
     * Get all vehicle types with pagination
     * 
     * @param pageable Pagination parameters
     * @return Page of vehicle type summaries
     */
    Page<VehicleTypeSummaryResponse> getAllVehicleTypes(Pageable pageable);

    /**
     * Get all active vehicle types
     * 
     * @return List of active vehicle types
     */
    List<VehicleTypeSummaryResponse> getActiveVehicleTypes();

    /**
     * Get vehicle type by ID
     * 
     * @param id Vehicle type ID
     * @return Vehicle type details
     */
    VehicleTypeResponse getVehicleTypeById(Long id);

    /**
     * Search vehicle types by name
     * 
     * @param typeName Search term
     * @return List of matching vehicle types
     */
    List<VehicleTypeSummaryResponse> searchVehicleTypesByName(String typeName);

    /**
     * Get vehicle types by seating capacity
     * 
     * @param capacity Seating capacity
     * @return List of vehicle types with specified capacity
     */
    List<VehicleTypeSummaryResponse> getVehicleTypesByCapacity(Integer capacity);

    /**
     * Get vehicle types by AC availability
     * 
     * @param hasAc AC availability
     * @return List of vehicle types
     */
    List<VehicleTypeSummaryResponse> getVehicleTypesByAc(Boolean hasAc);

    /**
     * Create new vehicle type
     * 
     * @param request Vehicle type creation request
     * @return Created vehicle type
     */
    VehicleTypeResponse createVehicleType(VehicleTypeRequest request);

    /**
     * Update existing vehicle type
     * 
     * @param id Vehicle type ID
     * @param request Vehicle type update request
     * @return Updated vehicle type
     */
    VehicleTypeResponse updateVehicleType(Long id, VehicleTypeRequest request);

    /**
     * Delete vehicle type
     * 
     * @param id Vehicle type ID
     */
    void deleteVehicleType(Long id);

    /**
     * Check if vehicle type name exists
     * 
     * @param typeName Vehicle type name
     * @return true if exists, false otherwise
     */
    boolean existsByTypeName(String typeName);

    /**
     * Check if vehicle type name exists excluding specific ID
     * 
     * @param typeName Vehicle type name
     * @param excludeId ID to exclude from check
     * @return true if exists, false otherwise
     */
    boolean existsByTypeNameAndIdNot(String typeName, Long excludeId);
}

// Made with Bob
