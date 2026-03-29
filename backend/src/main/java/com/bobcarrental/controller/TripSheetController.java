package com.bobcarrental.controller;

import com.bobcarrental.dto.common.ApiResponse;
import com.bobcarrental.dto.common.PageResponse;
import com.bobcarrental.dto.request.TripSheetRequest;
import com.bobcarrental.dto.response.TripSheetResponse;
import com.bobcarrental.dto.response.TripSheetSummaryResponse;
import com.bobcarrental.service.TripSheetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * REST Controller for TripSheet management
 * Handles CRUD operations and trip workflow
 * 
 * @author Bob Car Rental System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/trip-sheets")
@RequiredArgsConstructor
public class TripSheetController {

    private final TripSheetService tripSheetService;

    /**
     * Get all trip sheets with pagination
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<PageResponse<TripSheetSummaryResponse>>> getAllTripSheets(
            @PageableDefault(size = 20, sort = "trpDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Fetching all trip sheets with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<TripSheetSummaryResponse> tripSheetPage = tripSheetService.getAllTripSheets(pageable);
        PageResponse<TripSheetSummaryResponse> pageResponse = PageResponse.of(tripSheetPage);
        
        return ResponseEntity.ok(ApiResponse.success("Trip sheets retrieved successfully", pageResponse));
    }

    /**
     * Get trip sheet by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<TripSheetResponse>> getTripSheetById(@PathVariable Long id) {
        log.info("Fetching trip sheet with id: {}", id);
        
        TripSheetResponse tripSheet = tripSheetService.getTripSheetById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Trip sheet retrieved successfully", tripSheet));
    }

    /**
     * Get trip sheet by trip number
     */
    @GetMapping("/by-number/{tripNo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<TripSheetResponse>> getTripSheetByTripNo(@PathVariable String tripNo) {
        log.info("Fetching trip sheet with trip number: {}", tripNo);
        
        TripSheetResponse tripSheet = tripSheetService.getTripSheetByTripNo(tripNo);
        
        return ResponseEntity.ok(ApiResponse.success("Trip sheet retrieved successfully", tripSheet));
    }

    /**
     * Get trip sheets by booking ID
     */
    @GetMapping("/by-booking/{bookingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<TripSheetSummaryResponse>>> getTripSheetsByBooking(
            @PathVariable Long bookingId) {
        
        log.info("Fetching trip sheets for booking id: {}", bookingId);
        
        List<TripSheetSummaryResponse> tripSheets = tripSheetService.getTripSheetsByBooking(bookingId);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d trip sheets for booking", tripSheets.size()), tripSheets));
    }

    /**
     * Get trip sheets by status
     */
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<TripSheetSummaryResponse>>> getTripSheetsByStatus(
            @PathVariable String status) {
        
        log.info("Fetching trip sheets with status: {}", status);
        
        List<TripSheetSummaryResponse> tripSheets = tripSheetService.getTripSheetsByStatus(status);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d trip sheets with status %s", tripSheets.size(), status), tripSheets));
    }

    /**
     * Get trip sheets by date range
     */
    @GetMapping("/by-date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<TripSheetSummaryResponse>>> getTripSheetsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Fetching trip sheets between {} and {}", startDate, endDate);
        
        List<TripSheetSummaryResponse> tripSheets = tripSheetService.getTripSheetsByDateRange(startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d trip sheets in date range", tripSheets.size()), tripSheets));
    }

    /**
     * Get active trip sheets (OPEN or STARTED)
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<TripSheetSummaryResponse>>> getActiveTripSheets() {
        log.info("Fetching active trip sheets");
        
        List<TripSheetSummaryResponse> tripSheets = tripSheetService.getActiveTripSheets();
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d active trip sheets", tripSheets.size()), tripSheets));
    }

    /**
     * Get completed trip sheets (FINISHED)
     */
    @GetMapping("/completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<List<TripSheetSummaryResponse>>> getCompletedTripSheets() {
        log.info("Fetching completed trip sheets");
        
        List<TripSheetSummaryResponse> tripSheets = tripSheetService.getCompletedTripSheets();
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d completed trip sheets", tripSheets.size()), tripSheets));
    }

    /**
     * Get unbilled trip sheets
     */
    @GetMapping("/unbilled")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TripSheetSummaryResponse>>> getUnbilledTripSheets() {
        log.info("Fetching unbilled trip sheets");
        
        List<TripSheetSummaryResponse> tripSheets = tripSheetService.getUnbilledTripSheets();
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d unbilled trip sheets", tripSheets.size()), tripSheets));
    }

    /**
     * Create new trip sheet
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<TripSheetResponse>> createTripSheet(
            @Valid @RequestBody TripSheetRequest request) {
        
        log.info("Creating new trip sheet: {}", request.getTrpNum());
        
        TripSheetResponse tripSheet = tripSheetService.createTripSheet(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip sheet created successfully", tripSheet));
    }

    /**
     * Update existing trip sheet
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<TripSheetResponse>> updateTripSheet(
            @PathVariable Long id,
            @Valid @RequestBody TripSheetRequest request) {
        
        log.info("Updating trip sheet with id: {}", id);
        
        TripSheetResponse tripSheet = tripSheetService.updateTripSheet(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Trip sheet updated successfully", tripSheet));
    }

    /**
     * Start a trip (change status from OPEN to STARTED)
     */
    @PatchMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<TripSheetResponse>> startTrip(@PathVariable Long id) {
        log.info("Starting trip with id: {}", id);
        
        TripSheetResponse tripSheet = tripSheetService.startTrip(id);
        
        return ResponseEntity.ok(ApiResponse.success("Trip started successfully", tripSheet));
    }

    /**
     * Finish a trip (change status from STARTED to FINISHED)
     * Calculates final fare based on actual km and time
     */
    @PatchMapping("/{id}/finish")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<TripSheetResponse>> finishTrip(
            @PathVariable Long id,
            @RequestParam Double endKm,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        
        log.info("Finishing trip with id: {}, endKm: {}, endDate: {}, endTime: {}", 
                id, endKm, endDate, endTime);
        
        TripSheetResponse tripSheet = tripSheetService.finishTrip(id, endKm, endDate, endTime);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Trip finished successfully. Fare calculated.", tripSheet));
    }

    /**
     * Calculate fare for a trip sheet
     */
    @PostMapping("/{id}/calculate-fare")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<TripSheetResponse>> calculateFare(@PathVariable Long id) {
        log.info("Calculating fare for trip sheet with id: {}", id);
        
        TripSheetResponse tripSheet = tripSheetService.calculateFare(id);
        
        return ResponseEntity.ok(ApiResponse.success("Fare calculated successfully", tripSheet));
    }

    /**
     * Delete trip sheet
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTripSheet(@PathVariable Long id) {
        log.info("Deleting trip sheet with id: {}", id);
        
        tripSheetService.deleteTripSheet(id);
        
        return ResponseEntity.ok(ApiResponse.success("Trip sheet deleted successfully", null));
    }

    /**
     * Check if trip number exists
     */
    @GetMapping("/validate/trip-no")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<Boolean>> validateTripNo(@RequestParam String tripNo) {
        log.info("Validating trip number: {}", tripNo);
        
        boolean exists = tripSheetService.existsByTripNo(tripNo);
        
        return ResponseEntity.ok(ApiResponse.success(
                exists ? "Trip number already exists" : "Trip number is available",
                !exists));
    }
}

// Made with Bob
