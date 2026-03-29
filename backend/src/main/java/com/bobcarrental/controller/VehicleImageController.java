package com.bobcarrental.controller;

import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.request.VehicleImageRequest;
import com.bobcarrental.dto.response.VehicleImageResponse;
import com.bobcarrental.service.VehicleImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for vehicle image operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/vehicleimages")
@RequiredArgsConstructor
public class VehicleImageController {

    private final VehicleImageService vehicleImageService;

    /**
     * Get all vehicle images with pagination
     * GET /api/v1/vehicleimages
     */
    @GetMapping
    public ResponseEntity<Page<VehicleImageResponse>> getAllVehicleImages(
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("GET /api/v1/vehicleimages - Getting all vehicle images");
        Page<VehicleImageResponse> images = vehicleImageService.getAllVehicleImages(pageable);
        return ResponseEntity.ok(images);
    }

    /**
     * Get vehicle image by ID
     * GET /api/v1/vehicleimages/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleImageResponse> getVehicleImageById(@PathVariable Long id) {
        log.info("GET /api/v1/vehicleimages/{} - Getting vehicle image by id", id);
        VehicleImageResponse image = vehicleImageService.getVehicleImageById(id);
        return ResponseEntity.ok(image);
    }

    /**
     * Get all images for a specific vehicle type
     * GET /api/v1/vehicleimages/vehicletype/{vehicleTypeId}
     */
    @GetMapping("/vehicletype/{vehicleTypeId}")
    public ResponseEntity<List<VehicleImageResponse>> getImagesByVehicleType(
            @PathVariable Long vehicleTypeId) {
        log.info("GET /api/v1/vehicleimages/vehicletype/{} - Getting images for vehicle type", vehicleTypeId);
        List<VehicleImageResponse> images = vehicleImageService.getImagesByVehicleType(vehicleTypeId);
        return ResponseEntity.ok(images);
    }

    /**
     * Get primary image for a vehicle type
     * GET /api/v1/vehicleimages/vehicletype/{vehicleTypeId}/primary
     */
    @GetMapping("/vehicletype/{vehicleTypeId}/primary")
    public ResponseEntity<VehicleImageResponse> getPrimaryImageByVehicleType(
            @PathVariable Long vehicleTypeId) {
        log.info("GET /api/v1/vehicleimages/vehicletype/{}/primary - Getting primary image", vehicleTypeId);
        VehicleImageResponse image = vehicleImageService.getPrimaryImageByVehicleType(vehicleTypeId);
        return ResponseEntity.ok(image);
    }

    /**
     * Create new vehicle image
     * POST /api/v1/vehicleimages
     * Requires ADMIN role
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleImageResponse>> createVehicleImage(
            @Valid @RequestBody VehicleImageRequest request) {
        log.info("POST /api/v1/vehicleimages - Creating vehicle image");
        VehicleImageResponse createdImage = vehicleImageService.createVehicleImage(request);
        
        ApiResponse<VehicleImageResponse> response = ApiResponse.<VehicleImageResponse>builder()
                .success(true)
                .message("Vehicle image created successfully")
                .data(createdImage)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update existing vehicle image
     * PUT /api/v1/vehicleimages/{id}
     * Requires ADMIN role
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleImageResponse>> updateVehicleImage(
            @PathVariable Long id,
            @Valid @RequestBody VehicleImageRequest request) {
        log.info("PUT /api/v1/vehicleimages/{} - Updating vehicle image", id);
        VehicleImageResponse updatedImage = vehicleImageService.updateVehicleImage(id, request);
        
        ApiResponse<VehicleImageResponse> response = ApiResponse.<VehicleImageResponse>builder()
                .success(true)
                .message("Vehicle image updated successfully")
                .data(updatedImage)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Set image as primary for its vehicle type
     * PATCH /api/v1/vehicleimages/{id}/set-primary
     * Requires ADMIN role
     */
    @PatchMapping("/{id}/set-primary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleImageResponse>> setPrimaryImage(@PathVariable Long id) {
        log.info("PATCH /api/v1/vehicleimages/{}/set-primary - Setting image as primary", id);
        VehicleImageResponse updatedImage = vehicleImageService.setPrimaryImage(id);
        
        ApiResponse<VehicleImageResponse> response = ApiResponse.<VehicleImageResponse>builder()
                .success(true)
                .message("Image set as primary successfully")
                .data(updatedImage)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Reorder images for a vehicle type
     * PUT /api/v1/vehicleimages/vehicletype/{vehicleTypeId}/reorder
     * Requires ADMIN role
     */
    @PutMapping("/vehicletype/{vehicleTypeId}/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<VehicleImageResponse>>> reorderImages(
            @PathVariable Long vehicleTypeId,
            @RequestBody List<Long> imageIds) {
        log.info("PUT /api/v1/vehicleimages/vehicletype/{}/reorder - Reordering images", vehicleTypeId);
        List<VehicleImageResponse> reorderedImages = vehicleImageService.reorderImages(vehicleTypeId, imageIds);
        
        ApiResponse<List<VehicleImageResponse>> response = ApiResponse.<List<VehicleImageResponse>>builder()
                .success(true)
                .message("Images reordered successfully")
                .data(reorderedImages)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete vehicle image
     * DELETE /api/v1/vehicleimages/{id}
     * Requires ADMIN role
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVehicleImage(@PathVariable Long id) {
        log.info("DELETE /api/v1/vehicleimages/{} - Deleting vehicle image", id);
        vehicleImageService.deleteVehicleImage(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Vehicle image deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete all images for a vehicle type
     * DELETE /api/v1/vehicleimages/vehicletype/{vehicleTypeId}
     * Requires ADMIN role
     */
    @DeleteMapping("/vehicletype/{vehicleTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteImagesByVehicleType(@PathVariable Long vehicleTypeId) {
        log.info("DELETE /api/v1/vehicleimages/vehicletype/{} - Deleting all images for vehicle type", vehicleTypeId);
        vehicleImageService.deleteImagesByVehicleType(vehicleTypeId);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("All images for vehicle type deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
}

// Made with Bob
