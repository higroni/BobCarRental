package com.bobcarrental.controller;

import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.common.PageResponse;
import com.bobcarrental.dto.request.StandardFareRequest;
import com.bobcarrental.dto.response.StandardFareResponse;
import com.bobcarrental.service.StandardFareService;
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
import java.util.Map;

/**
 * REST controller for standard fare operations (Admin only)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/standard-fares")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StandardFareController {

    private final StandardFareService standardFareService;

    /**
     * Get all standard fares with pagination
     * GET /api/v1/standard-fares
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StandardFareResponse>>> getAllStandardFares(
            @PageableDefault(size = 20, sort = "vehicleType.typeDesc", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("GET /api/v1/standard-fares - Getting all standard fares");
        Page<StandardFareResponse> fares = standardFareService.getAllStandardFares(pageable);
        
        PageResponse<StandardFareResponse> pageResponse = PageResponse.of(fares);
        ApiResponse<PageResponse<StandardFareResponse>> response = ApiResponse.<PageResponse<StandardFareResponse>>builder()
                .success(true)
                .message("Standard fares retrieved successfully")
                .data(pageResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get standard fare by ID
     * GET /api/v1/standard-fares/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StandardFareResponse>> getStandardFareById(@PathVariable Long id) {
        log.info("GET /api/v1/standard-fares/{} - Getting standard fare by id", id);
        StandardFareResponse fare = standardFareService.getStandardFareById(id);
        
        ApiResponse<StandardFareResponse> response = ApiResponse.<StandardFareResponse>builder()
                .success(true)
                .message("Standard fare retrieved successfully")
                .data(fare)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get standard fares by vehicle type
     * GET /api/v1/standardfares/vehicletype/{vehicleTypeId}
     */
    @GetMapping("/vehicletype/{vehicleTypeId}")
    public ResponseEntity<List<StandardFareResponse>> getStandardFaresByVehicleType(
            @PathVariable Long vehicleTypeId) {
        log.info("GET /api/v1/standardfares/vehicletype/{} - Getting standard fares for vehicle type", vehicleTypeId);
        List<StandardFareResponse> fares = standardFareService.getStandardFaresByVehicleType(vehicleTypeId);
        return ResponseEntity.ok(fares);
    }

    /**
     * Get standard fare by vehicle type and fare type
     * GET /api/v1/standardfares/vehicletype/{vehicleTypeId}/faretype/{fareType}
     */
    @GetMapping("/vehicletype/{vehicleTypeId}/faretype/{fareType}")
    public ResponseEntity<StandardFareResponse> getStandardFareByVehicleTypeAndFareType(
            @PathVariable Long vehicleTypeId,
            @PathVariable String fareType) {
        log.info("GET /api/v1/standardfares/vehicletype/{}/faretype/{} - Getting standard fare", 
            vehicleTypeId, fareType);
        StandardFareResponse fare = standardFareService.getStandardFareByVehicleTypeAndFareType(
            vehicleTypeId, fareType);
        return ResponseEntity.ok(fare);
    }

    /**
     * Get standard fares by fare type
     * GET /api/v1/standardfares/faretype/{fareType}
     */
    @GetMapping("/faretype/{fareType}")
    public ResponseEntity<List<StandardFareResponse>> getStandardFaresByFareType(
            @PathVariable String fareType) {
        log.info("GET /api/v1/standardfares/faretype/{} - Getting standard fares by fare type", fareType);
        List<StandardFareResponse> fares = standardFareService.getStandardFaresByFareType(fareType);
        return ResponseEntity.ok(fares);
    }

    /**
     * Get active standard fares
     * GET /api/v1/standardfares/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<StandardFareResponse>> getActiveStandardFares() {
        log.info("GET /api/v1/standardfares/active - Getting active standard fares");
        List<StandardFareResponse> fares = standardFareService.getActiveStandardFares();
        return ResponseEntity.ok(fares);
    }

    /**
     * Create new standard fare
     * POST /api/v1/standardfares
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StandardFareResponse>> createStandardFare(
            @Valid @RequestBody StandardFareRequest request) {
        log.info("POST /api/v1/standardfares - Creating standard fare");
        StandardFareResponse createdFare = standardFareService.createStandardFare(request);
        
        ApiResponse<StandardFareResponse> response = ApiResponse.<StandardFareResponse>builder()
                .success(true)
                .message("Standard fare created successfully")
                .data(createdFare)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update existing standard fare
     * PUT /api/v1/standardfares/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StandardFareResponse>> updateStandardFare(
            @PathVariable Long id,
            @Valid @RequestBody StandardFareRequest request) {
        log.info("PUT /api/v1/standardfares/{} - Updating standard fare", id);
        StandardFareResponse updatedFare = standardFareService.updateStandardFare(id, request);
        
        ApiResponse<StandardFareResponse> response = ApiResponse.<StandardFareResponse>builder()
                .success(true)
                .message("Standard fare updated successfully")
                .data(updatedFare)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Activate standard fare
     * PATCH /api/v1/standardfares/{id}/activate
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<StandardFareResponse>> activateStandardFare(@PathVariable Long id) {
        log.info("PATCH /api/v1/standardfares/{}/activate - Activating standard fare", id);
        StandardFareResponse updatedFare = standardFareService.activateStandardFare(id);
        
        ApiResponse<StandardFareResponse> response = ApiResponse.<StandardFareResponse>builder()
                .success(true)
                .message("Standard fare activated successfully")
                .data(updatedFare)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate standard fare
     * PATCH /api/v1/standardfares/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<StandardFareResponse>> deactivateStandardFare(@PathVariable Long id) {
        log.info("PATCH /api/v1/standardfares/{}/deactivate - Deactivating standard fare", id);
        StandardFareResponse updatedFare = standardFareService.deactivateStandardFare(id);
        
        ApiResponse<StandardFareResponse> response = ApiResponse.<StandardFareResponse>builder()
                .success(true)
                .message("Standard fare deactivated successfully")
                .data(updatedFare)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete standard fare
     * DELETE /api/v1/standardfares/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStandardFare(@PathVariable Long id) {
        log.info("DELETE /api/v1/standardfares/{} - Deleting standard fare", id);
        standardFareService.deleteStandardFare(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Standard fare deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if standard fare exists for vehicle type and fare type
     * GET /api/v1/standardfares/exists?vehicleTypeId={id}&fareType={type}
     */
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> checkStandardFareExists(
            @RequestParam Long vehicleTypeId,
            @RequestParam String fareType) {
        log.info("GET /api/v1/standardfares/exists?vehicleTypeId={}&fareType={} - Checking existence", 
            vehicleTypeId, fareType);
        boolean exists = standardFareService.existsByVehicleTypeAndFareType(vehicleTypeId, fareType);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}

// Made with Bob
