package com.bobcarrental.service;

import com.bobcarrental.dto.request.VehicleImageRequest;
import com.bobcarrental.dto.response.VehicleImageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for vehicle image operations
 */
public interface VehicleImageService {

    /**
     * Get all vehicle images with pagination
     */
    Page<VehicleImageResponse> getAllVehicleImages(Pageable pageable);

    /**
     * Get vehicle image by ID
     */
    VehicleImageResponse getVehicleImageById(Long id);

    /**
     * Get all images for a specific vehicle type
     */
    List<VehicleImageResponse> getImagesByVehicleType(Long vehicleTypeId);

    /**
     * Get primary image for a vehicle type
     */
    VehicleImageResponse getPrimaryImageByVehicleType(Long vehicleTypeId);

    /**
     * Create new vehicle image
     */
    VehicleImageResponse createVehicleImage(VehicleImageRequest request);

    /**
     * Update existing vehicle image
     */
    VehicleImageResponse updateVehicleImage(Long id, VehicleImageRequest request);

    /**
     * Set image as primary for its vehicle type
     */
    VehicleImageResponse setPrimaryImage(Long id);

    /**
     * Reorder images for a vehicle type
     */
    List<VehicleImageResponse> reorderImages(Long vehicleTypeId, List<Long> imageIds);

    /**
     * Delete vehicle image
     */
    void deleteVehicleImage(Long id);

    /**
     * Delete all images for a vehicle type
     */
    void deleteImagesByVehicleType(Long vehicleTypeId);
}

// Made with Bob
