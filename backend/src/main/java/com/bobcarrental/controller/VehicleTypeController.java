package com.bobcarrental.controller;

import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.common.PageResponse;
import com.bobcarrental.dto.request.VehicleTypeRequest;
import com.bobcarrental.dto.response.VehicleTypeResponse;
import com.bobcarrental.dto.response.VehicleTypeSummaryResponse;
import com.bobcarrental.service.VehicleTypeService;
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
 * REST Controller for VehicleType management
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/vehicle-types")
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<PageResponse<VehicleTypeSummaryResponse>>> getAllVehicleTypes(
            @PageableDefault(size = 20, sort = "typeDesc", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.info("Fetching all vehicle types with pagination");
        Page<VehicleTypeSummaryResponse> vehicleTypePage = vehicleTypeService.getAllVehicleTypes(pageable);
        PageResponse<VehicleTypeSummaryResponse> pageResponse = PageResponse.of(vehicleTypePage);
        
        return ResponseEntity.ok(ApiResponse.success("Vehicle types retrieved successfully", pageResponse));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<VehicleTypeSummaryResponse>>> getActiveVehicleTypes() {
        log.info("Fetching all active vehicle types");
        List<VehicleTypeSummaryResponse> vehicleTypes = vehicleTypeService.getActiveVehicleTypes();
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d active vehicle types", vehicleTypes.size()), vehicleTypes));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<VehicleTypeResponse>> getVehicleTypeById(@PathVariable Long id) {
        log.info("Fetching vehicle type with id: {}", id);
        VehicleTypeResponse vehicleType = vehicleTypeService.getVehicleTypeById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Vehicle type retrieved successfully", vehicleType));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<VehicleTypeSummaryResponse>>> searchVehicleTypes(
            @RequestParam String typeName) {
        
        log.info("Searching vehicle types with name containing: {}", typeName);
        List<VehicleTypeSummaryResponse> vehicleTypes = vehicleTypeService.searchVehicleTypesByName(typeName);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d vehicle types matching '%s'", vehicleTypes.size(), typeName), vehicleTypes));
    }

    @GetMapping("/by-capacity")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<VehicleTypeSummaryResponse>>> getVehicleTypesByCapacity(
            @RequestParam Integer capacity) {
        
        log.info("Fetching vehicle types with capacity: {}", capacity);
        List<VehicleTypeSummaryResponse> vehicleTypes = vehicleTypeService.getVehicleTypesByCapacity(capacity);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d vehicle types with %d seats", vehicleTypes.size(), capacity), vehicleTypes));
    }

    @GetMapping("/by-ac")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<VehicleTypeSummaryResponse>>> getVehicleTypesByAc(
            @RequestParam Boolean hasAc) {
        
        log.info("Fetching vehicle types with AC: {}", hasAc);
        List<VehicleTypeSummaryResponse> vehicleTypes = vehicleTypeService.getVehicleTypesByAc(hasAc);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d %s vehicle types", vehicleTypes.size(), hasAc ? "AC" : "Non-AC"), vehicleTypes));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleTypeResponse>> createVehicleType(
            @Valid @RequestBody VehicleTypeRequest request) {
        
        log.info("Creating new vehicle type: {}", request.getTypeName());
        VehicleTypeResponse vehicleType = vehicleTypeService.createVehicleType(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle type created successfully", vehicleType));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleTypeResponse>> updateVehicleType(
            @PathVariable Long id,
            @Valid @RequestBody VehicleTypeRequest request) {
        
        log.info("Updating vehicle type with id: {}", id);
        VehicleTypeResponse vehicleType = vehicleTypeService.updateVehicleType(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Vehicle type updated successfully", vehicleType));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVehicleType(@PathVariable Long id) {
        log.info("Deleting vehicle type with id: {}", id);
        vehicleTypeService.deleteVehicleType(id);
        
        return ResponseEntity.ok(ApiResponse.success("Vehicle type deleted successfully", null));
    }

    @GetMapping("/validate/type-name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> validateTypeName(@RequestParam String typeName) {
        log.info("Validating vehicle type name: {}", typeName);
        boolean exists = vehicleTypeService.existsByTypeName(typeName);
        
        return ResponseEntity.ok(ApiResponse.success(
                exists ? "Vehicle type name already exists" : "Vehicle type name is available", !exists));
    }
}

// Made with Bob
