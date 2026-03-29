package com.bobcarrental.service;

import com.bobcarrental.dto.request.StandardFareRequest;
import com.bobcarrental.dto.response.StandardFareResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for standard fare operations
 */
public interface StandardFareService {

    /**
     * Get all standard fares with pagination
     */
    Page<StandardFareResponse> getAllStandardFares(Pageable pageable);

    /**
     * Get standard fare by ID
     */
    StandardFareResponse getStandardFareById(Long id);

    /**
     * Get standard fares by vehicle type
     */
    List<StandardFareResponse> getStandardFaresByVehicleType(Long vehicleTypeId);

    /**
     * Get standard fare by vehicle type and fare type
     */
    StandardFareResponse getStandardFareByVehicleTypeAndFareType(Long vehicleTypeId, String fareType);

    /**
     * Get standard fares by fare type
     */
    List<StandardFareResponse> getStandardFaresByFareType(String fareType);

    /**
     * Get active standard fares
     */
    List<StandardFareResponse> getActiveStandardFares();

    /**
     * Create new standard fare
     */
    StandardFareResponse createStandardFare(StandardFareRequest request);

    /**
     * Update existing standard fare
     */
    StandardFareResponse updateStandardFare(Long id, StandardFareRequest request);

    /**
     * Activate standard fare
     */
    StandardFareResponse activateStandardFare(Long id);

    /**
     * Deactivate standard fare
     */
    StandardFareResponse deactivateStandardFare(Long id);

    /**
     * Delete standard fare
     */
    void deleteStandardFare(Long id);

    /**
     * Check if standard fare exists for vehicle type and fare type
     */
    boolean existsByVehicleTypeAndFareType(Long vehicleTypeId, String fareType);
}

// Made with Bob
